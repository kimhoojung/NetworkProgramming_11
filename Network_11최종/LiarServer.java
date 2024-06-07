

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;

class LiarServer extends Thread implements ActionListener {
    ServerSocket ss;
    Socket s;
    int port = 3000;
    String portN;
    Vector<OneClientModul> v = new Vector<OneClientModul>();
    OneClientModul ocm;
    Thread gameThread = new Thread();
    Thread serverThread;
    ServerUi sui;
    String msg;
    String currentTopic = "10초초과";
    ArrayList voteList;

    LiarServer(ServerUi sui) {
        try {
            this.sui = sui;
            this.portN = sui.ui.port;
            port = Integer.parseInt(portN);
            ss = new ServerSocket(port);
            System.out.println(ss); // 객체 ss의 정보를 출력
            sui.setTitle("ip: " + InetAddress.getLocalHost().getHostAddress() + ", port: " + port + " 서버관리자");
        } catch (IOException e) {
            e.printStackTrace(); //예외 출력
        }
        serverThread = new Thread(this); //현재 객체를 실행할 서버 스레드 생성
        serverThread.start();
        this.sui = sui;
        this.s = sui.s;
        actionListener();
    }


    void kickout() {
        String kickId = String.valueOf(sui.idBox.getSelectedItem()); //강퇴할 클라이언트  ID
        for (OneClientModul ocm : v) { //클라이언트 모듈 벡터 순회하면서 ID일치하면 강퇴
            if (ocm.chatId.equals(kickId)) {
                ocm.broadcast(ocm.chatId + "님이 강퇴당했습니다..");
                v.remove(ocm); //벡터에서 제거
                ocm.closeAll();
                break;
            }
        }
    }

    @Override
    public void run() { //서버가 클라 연결 수락, 게임 시작
        if (currentThread().equals(serverThread)) {
            try {
                while (true) {
                    s = ss.accept(); //클라이언트 연결 수락
                    OutputStream os = s.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(os); //데이터 송수신 스트림 생성
                    if (v.size() == 10) { //클라이언트가 10명이면 게임 진행X
                        dos.writeUTF("false");
                    } else if (gameThread.isAlive() == true) {
                        dos.writeUTF("true");
                        dos.writeUTF("3초후");
                        System.out.println("enterfalse");
                    } else if (v.size() < 10) {
                        dos.writeUTF("true");
                        ocm = new OneClientModul(this);
                        sui.idBox.addItem(ocm.chatId);
                        v.add(ocm);
                        ocm.start();
                    }
                }
            } catch (IOException ie) {
                pln(port + "번 포트 사용중.");
            } finally {
                try {
                    if (ss != null) ss.close();
                    System.out.println("서버다운");
                } catch (IOException ie) {
                }
            }
        }
        if (currentThread().equals(gameThread)) {
            try {
                if (v.size() > 1) {
                    ocm.broadcast("3초후 게임을 시작합니다.");
                    sleep(1000);
                    ocm.broadcast("2초후 게임을 시작합니다.");
                    sleep(1000);
                    ocm.broadcast("1초후 게임을 시작합니다.");
                    sleep(1000);
                    voteList = new ArrayList(); //투표리스트 초기화
                    new GameManager(this);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void sleepTh(int i) {//클라가 입력하는 10초동안 서버 스레드 멈춤
        try {
            currentThread().sleep(i * 1000);
        } catch (InterruptedException e) {
        }
    }


    void actionListener() {
        Action enter = new AbstractAction() { //엔터키 눌렸을 때 실행
            @Override
            public void actionPerformed(ActionEvent e) {
                msg = sui.chatTf.getText(); //텍스트 필드에서 입려된 텍스트를 가져옴
                msg = msg.trim(); // 앞뒤 공백 제거
                msg = "관리자 : " + msg;
                sui.chatTf.setText(null); // 텍스트필드 비워줌
                if (v.size() != 0) {
                    ocm.broadcast(msg);
                } else {
                    sui.txta.append("입장한 유저가 없습니다.\n");
                }
            }
        };
        sui.chatTf.addActionListener(enter);
        sui.kickBtn.addActionListener(this); //현재 객체 참조, actionPerformed 메서드 호출
        sui.startBtn.addActionListener(this);
        sui.endBtn.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(sui.kickBtn)) { //강퇴기능
            kickout();
        }
        if (e.getSource().equals(sui.startBtn)) { //게임시작버튼 확인
            if (v.size() != 0) {
                sui.startBtn.setEnabled(false); //게임시작화면 비활성화 -> 중복 시작 방지
                gameThread = new Thread(this);
                gameThread.start(); //별도의 스레드에서 게임 로직이 시작
            }
        }
        if (e.getSource().equals(sui.endBtn)) {

            System.exit(0);

        }
    }

    void pln(String str) {
        System.out.println(str);
    }

}                                                                                               //라이어서버


class OneClientModul extends Thread {                                                           //원클모듈
    LiarServer ls;
    Socket s;
    InputStream is;
    OutputStream os;
    DataInputStream dis;
    DataOutputStream dos;
    String chatId;
    ServerUi sui;

    OneClientModul(LiarServer ls) {
        this.ls = ls;
        this.s = ls.s;
        this.sui = ls.sui;
        try {
            is = s.getInputStream();
            os = s.getOutputStream();
            dis = new DataInputStream(is);
            dos = new DataOutputStream(os);
            System.out.println("ocm 입장");
            chatId = dis.readUTF(); //클라이언트로부터 ID읽기
            System.out.println("Client ID : " + chatId);
            if (chatId.equals("enterfalse")) { //enterfalse이면 모든 스트림과 소켓 닫기
                closeAll();
            } else {
                String enterId = chatId;
                Boolean checkId = true;
                for (OneClientModul ocm : ls.v) { //ID중복 체크 변수
                    if (ocm.chatId.equals(enterId)) {
                        System.out.println("중복 아이디 찾는중");
                        checkId = false;
                        continue;
                    }
                }
                if (checkId == false) {
                    System.out.println("중복아이디 있음");
                    dos.writeUTF("dupid");
                    System.out.println("dupid");
                    closeAll();
                } else {
                    System.out.println("중복아이디 없음");
                    dos.writeUTF("true");
                }
            }
        } catch (IOException ie) {
        }
    }

    public void run() {
        listen();
    }

    void listen() {
        String msg = "";
        //int i;
        try {
            broadcast(chatId + " 님이 입장하셨습니다. (현재 인원: " + ls.v.size() + "명)");
            while (true) {
                msg = dis.readUTF();
                if (msg.startsWith("currentTopic")) { //주제
                    if (msg != null) {
                        ls.currentTopic = msg.substring(9);
                    }
                    System.out.println(ls.currentTopic);
                } else if (msg.startsWith("cVote")) {//투표 내용
                    msg = msg.substring(5);
                    ls.voteList.add(msg);
                    System.out.println("투표 : " + msg);
                    if (ls.voteList.size() == ls.v.size()) {
                        ls.gameThread.interrupt();
                    }
                } else if (msg.equals("enterfalse")) {//아무것도 안함
                } else {//그 외는 메세지를 모든 클라에 브로드캐스트
                    broadcast(msg);
                }
            }
        } catch (IOException ie) {
            ls.v.remove(this); //클라 목록에서 제거
            broadcast(chatId + " 님이 퇴장하셨습니다. (현재 인원: " + ls.v.size() + "명)");
            ls.sui.idBox.removeItem(chatId);//UI에서 클라 ID제거
        } finally {
            closeAll();
        }
    }

    void broadcast(String msg) {
        try {
            for (OneClientModul ocm : ls.v) {  //모든 클라에게 메세지 전송
                ocm.dos.writeUTF(msg);
                ocm.dos.flush();
            }
            if (msg.startsWith("gm")) {//gm으로 시작하면 gm 제거한 나머지 메세지를 ui에 추가
                msg = msg.substring(2);
                sui.txta.append(msg + "\n");
                sui.sp.getVerticalScrollBar().setValue(sui.sp.getVerticalScrollBar().getMaximum());
            } else sui.txta.append(msg + "\n");
            sui.sp.getVerticalScrollBar().setValue(sui.sp.getVerticalScrollBar().getMaximum());
        } catch (IOException ie) {
        }
    }

    void closeAll() { //모든 스트림과 소켓 닫기 메서드
        try {
            if (dis != null) dis.close();
            if (dos != null) dos.close();
            if (is != null) is.close();
            if (os != null) os.close();
            if (s != null) s.close();
        } catch (IOException ie) {
        }
    }
}
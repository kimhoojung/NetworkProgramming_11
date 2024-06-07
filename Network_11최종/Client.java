import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.util.Timer;

												// 클라이언트 클래스 정의, Runnable 및 ActionListener 인터페이스 구현
class Client implements Runnable, ActionListener {
    String id, ip;
    int port = 0;
    InputStream is;
    OutputStream os;
    DataInputStream dis;
    DataOutputStream dos;
    Socket sk;
    LoginUi ui;
    int nop; 
    Vector<String> idList = new Vector<>();
    String lsnMsg, spkMsg;
    ClientUi cui;
    JFrame frame;
    JOptionPane jop = new JOptionPane();
    Thread listenTh;
    Thread chatTimeTh;

    // 채팅 입력 시 엔터키를 누르면 메시지를 전송하는 액션
    Action enter = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            spkMsg = cui.chatTf.getText();
            spkMsg = spkMsg.trim();
            speak(spkMsg);
            cui.chatTf.setText(null);
        }
    };

    Action enter2 = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            spkMsg = cui.chatTf.getText();
            spkMsg = spkMsg.trim();
            speak(spkMsg);
            cui.chatTf.setText(null);
            cui.chatTf.setEnabled(false);
            cui.chatTf.removeActionListener(enter2);
        }
    };

    Action enter3 = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            spkMsg = cui.chatTf.getText();
            spkMsg = spkMsg.trim();
            speak(spkMsg);
            speak("currentTopic" + spkMsg);
            cui.chatTf.setText(null);
            cui.chatTf.setEnabled(false);
            cui.chatTf.removeActionListener(enter3);
        }
    };

    Client(ClientUi cui) {
        this.cui = cui;  // 클라이언트 UI 참조 저장
        this.ui = cui.ui;  // 로그인 UI 참조 저장
        this.ip = cui.ip;  // 서버 IP 저장
        this.port = Integer.parseInt(cui.port);  // 서버 포트 저장
        this.id = cui.id;  // 사용자 ID 저장
        this.frame = cui;  // 프레임 참조 저장

        try {
            System.out.println(id + ip + port);
            sk = new Socket(ip, port);  // 서버에 소켓 연결 생성
            is = sk.getInputStream();  // 입력 스트림 생성
            os = sk.getOutputStream();  // 출력 스트림 생성
            dis = new DataInputStream(is);  // 데이터 입력 스트림 생성
            dos = new DataOutputStream(os);  // 데이터 출력 스트림 생성
            System.out.println("연결");
            String ent = dis.readUTF();  // 서버로부터 초기 메시지 수신
            System.out.println(ent);
            act();  // 클라이언트 UI 액션 설정

            if (ent.equals("falsefull")) {
                System.out.println("enterfalse");
                JOptionPane.showMessageDialog(null, "해당 서버의 인원이 가득 찼습니다", "인원 초과", 0);
                dos.writeUTF("enterfalse");  // 서버에 인원 초과 메시지 전송
                dos.flush();
                reLogin();  // 서버의 인원이 가득 찼을 경우 재로그인 처리
            } else {
                dos.writeUTF(id);  // 서버에 사용자 ID 전송
                ent = dis.readUTF();  // 서버로부터 메시지 수신
                System.out.println(ent);
                if (ent.equals("falseid")) {
                    System.out.println("enterfalse");
                    JOptionPane.showMessageDialog(null, "중복된 아이디가 있습니다.", "ID중복", 0);
                    dos.writeUTF("enterfalse");  // 서버에 ID 중복 메시지 전송
                    dos.flush();
                    reLogin();  // 중복된 아이디가 있을 경우 재로그인 처리
                } else if (ent.equals("3초후")) {
                    System.out.println("enterfalse");
                    JOptionPane.showMessageDialog(null, "해당 서버의 게임이 시작되었습니다.", "게임 시작", 0);
                    dos.writeUTF("enterfalse");  // 서버에 게임 시작 메시지 전송
                    dos.flush();
                    reLogin();  // 게임이 이미 시작되었을 때 재로그인 처리
                } else {
                    System.out.println("낫 폴스");
                    dos.flush();
                    listenTh = new Thread(this);  // 서버로부터 메시지를 수신할 스레드 생성
                    listenTh.start();  // 스레드 시작
                }
            }
        } catch (IOException ie) {
            System.out.println("Client ie: " + ie);
            JOptionPane.showMessageDialog(null, "아이피 또는 포트가 올바르지 않습니다.", "연결 오류", 0);
            reLogin();  // 잘못된 접근 시 재로그인 처리
        }
    }


    // 서버에서 받은 메시지에 대한 처리
    String protocol() throws IOException {
        if (lsnMsg.startsWith(id + ">>") & !lsnMsg.startsWith(id + " ")) {
            lsnMsg = lsnMsg.replaceFirst(id, "나 ");
            return lsnMsg;
        } else if (lsnMsg.startsWith(id + "님이 강퇴")) {
            sk.close();
            JOptionPane.showMessageDialog(null, "관리자에 의해 강퇴당하셨습니다.", "강퇴", 0);
            cui.dispose();
            ui.reopen();
            return "exit";
        } else if (lsnMsg.startsWith("gm")) {
            System.out.println(lsnMsg + "gm메세지");
            lsnMsg = lsnMsg.substring(2);
            System.out.println(lsnMsg);
            fromGm(lsnMsg);
            return null;
        } else {
            System.out.println(lsnMsg);
            return lsnMsg;
        }
    }

    // 클라이언트 UI 액션 설정
    void act() {
        cui.chatTf.addActionListener(enter);
        cui.endBtn.addActionListener(this);
    }

    // 게임 매니저로부터 받은 메시지 처리
    void fromGm(String lsnMsg) {
        if (lsnMsg.startsWith("liar:")) {
            cui.setTitle(id + "(으)로 게임중..(ip: " + ip + ", port: " + port + ")");
            if (lsnMsg.substring(5).equals(id)) {
                cui.topicTf.setFont(new Font("맑은 고딕", Font.BOLD, 16));
                cui.topicTf.setText("당신은 라이어입니다");
            }
        } else if (lsnMsg.startsWith("topic:")) {
            if (cui.topicTf.getText().equals("당신은 라이어입니다")) {
            } else {
                cui.topicTf.setText(lsnMsg.substring(6));
            }
        } else if (lsnMsg.startsWith("채팅락")) {
            cui.chatTf.setEnabled(false);
        } else if (lsnMsg.startsWith("채팅언락")) {
            if (lsnMsg.substring(4).equals(id)) {
                printTimer(cui.timeTf, 10);
                cui.textarea.append("상단 제한시간안에 제시어에 대해 설명해주세요.\n");
                cui.chatTf.setEnabled(true);
                chatTimeTh = new Thread(this);
                chatTimeTh.start();
                cui.chatTf.removeActionListener(enter);
                cui.chatTf.addActionListener(enter2);
            }
        } else if (lsnMsg.startsWith("votecom")) {
            if (lsnMsg.substring(7).equals(id)) {
                printTimer(cui.timeTf, 10);
                cui.textarea.append("10초안에 제시어를 추리하여 입력해주세요.\n");
                cui.chatTf.setEnabled(true);
                chatTimeTh = new Thread(this);
                chatTimeTh.start();
                cui.chatTf.removeActionListener(enter2);
                cui.chatTf.addActionListener(enter3);
            }
        } else if (lsnMsg.startsWith("올언락")) {
            cui.chatTf.setEnabled(true);
            cui.chatTf.setText("");
            cui.topicTf.setText("");
            cui.timeTf.setText("");
            cui.chatTf.removeActionListener(enter2);
            cui.chatTf.removeActionListener(enter3);
            cui.chatTf.addActionListener(enter);
            idList.removeAllElements();
            cui.setTitle(id + "(으)로 채팅중..(ip: " + ip + ", port: " + port + ")");
        } else if (lsnMsg.startsWith("list")) {
            idList.add(lsnMsg.substring(4));
        } else if (lsnMsg.startsWith("vote")) {
            System.out.println("vote입장");
            String vote = new VoteUi(this).getResult();
            System.out.println(vote);
            speak("cVote" + vote);
        } else if (lsnMsg.startsWith("result")) {
            lsnMsg = lsnMsg.substring(6);
            new Result(this, lsnMsg);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(cui.endBtn)) {
            try {
                sk.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            cui.dispose();
            ui.reopen();
        }
    }

    // 재로그인 처리
    void reLogin() {
        cui.dispose();
        cui.ui.reopen();
        cui.ui.clientBTn.doClick();
    }

    // 서버로 메시지 전송
    void speak(String str) {
        try {
            if (str.startsWith("liar")) {
                dos.writeUTF(str);
                dos.flush();
            } else if (str.startsWith("cVote")) {
                dos.writeUTF(str);
                dos.flush();
            } else {
                dos.writeUTF(id + ">> " + str);
                dos.flush();
            }
        } catch (IOException ie) {
            System.out.println("speak() ie: " + ie);
        }
    }

    @Override
    public void run() {
        if (Thread.currentThread().equals(listenTh)) {
            while (true) {
                String msg = null;
                msg = listen();
                if (msg != null) {
                    if (msg.equals("exit")) {
                        System.out.println("exit");
                        closeAll();
                        break;
                    } else {
                        cui.textarea.append(msg + "\n");
                        cui.sp.getVerticalScrollBar().setValue(cui.sp.getVerticalScrollBar().getMaximum());
                    }
                }
            }
        }
        if (Thread.currentThread().equals(chatTimeTh)) {
            try {
                Thread.currentThread().sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cui.chatTf.setEnabled(false);
        }
    }

    // 서버로부터 메시지 수신
    String listen() {
        lsnMsg = "";
        try {
            lsnMsg = dis.readUTF();
            System.out.println(lsnMsg);
            return protocol();
        } catch (IOException ie) {
            System.out.println(ie);
            JOptionPane.showMessageDialog(null, "서버와 연결이 끊겼습니다.");
            return "exit";
        }
    }

    // 타이머 출력
    void printTimer(JTextField tf, int i) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (i * 1000);
        final String[] time = new String[1];
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                long leftTime = endTime - currentTime;
                long leftSeconds = (leftTime / 1000) % 60;
                if (leftSeconds == 0) {
                    tf.setText("");
                    timer.cancel();
                }
                tf.setText(leftSeconds + "초");
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    // 모든 스트림과 소켓을 닫음
    void closeAll() {
        try {
            if (dis != null) dis.close();
            if (dos != null) dos.close();
            if (is != null) is.close();
            if (os != null) os.close();
            if (sk != null) sk.close();
            cui.dispose();
            cui.ui.reopen();
        } catch (IOException ie) {
        }
    }
}

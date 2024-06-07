

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

class ServerUi extends JFrame implements ActionListener {
    String port;
    LoginUi ui; //loginui객체 참고, 초기 설정 정보 가져옴
    Socket s; //서버소켓
    JTextArea txta = new JTextArea(); //채팅 메세지 표시할 텍스트 영역
    JTextField  chatTf; //채팅 입력란
    JScrollPane sp; //스크롤 기능 추가하기 위한 스크롤 패널
    JPanel  toP, chatP, btnP, txtP;
    Container cp; //컨테이너, 컨텐츠 영역 참조
    JButton startBtn, kickBtn, endBtn, clearBtn;
    JComboBox idBox; //사용자 아이디를 선택하기 위한 콤보박스

    ServerUi(LoginUi ui) { //생성자
        this.ui = ui; //전달받은 loginui객체를 멤버 변수 ui에 저장
        this.port=ui.port;
        init();
        setUi();
        new LiarServer(this); //liarserver객체 생성, 거기에다가 serverui객체를 전달해줌
    }

    void init() {
        cp = getContentPane();
        cp.setLayout(new BorderLayout());
        toP = new JPanel(new BorderLayout());
        cp.add(toP, BorderLayout.NORTH);

        txtP = new JPanel(new BorderLayout());
        txtP.add(txta, BorderLayout.CENTER);
        sp = new JScrollPane(txta, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //세로 스크롤 필요시 표시, 가로 스크롤은 X
        txta.setLineWrap(true);//텍스트 자동 줄바꿈
        txtP.add(sp);
        txta.setEditable(false); //텍스트 영역 편집 불가능하도록 설정
        cp.add(txtP, BorderLayout.CENTER);

        chatP = new JPanel(new BorderLayout());
        btnP = new JPanel(new GridLayout(1, 5));
        startBtn = new JButton("게임 시작!");
        clearBtn = new JButton("채팅 지우기");
        kickBtn = new JButton("강퇴");
        idBox = new JComboBox();
        endBtn = new JButton("종료");
        btnP.add(startBtn); //btnP에 버튼들 추가
        btnP.add(clearBtn);
        btnP.add(kickBtn);
        btnP.add(idBox);
        btnP.add(endBtn);
        chatTf = new JTextField(""); //빈 텍스트 필드 생성
        chatP.add(chatTf, BorderLayout.CENTER);
        chatP.add(btnP, BorderLayout.SOUTH);
        cp.add(chatP, BorderLayout.SOUTH);
        txta.setFont(new Font("HY견명조 보통",Font.BOLD,20));
        txta.setDisabledTextColor(Color.black);
        setUi();
        act();
    }

    void setUi() {
        setVisible(true);
        setTitle( port + "에서 채팅중..");
        setSize(700, 450);
        setLocationRelativeTo(null); // 창 위치 가운데로 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //창 닫을때 동작,닫으면 프로그램이 종료됨
    }

    void act() {
        endBtn.addActionListener(this);
        clearBtn.addActionListener(this);
        startBtn.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(clearBtn)) {
            txta.setText(null);
        }

    }


}
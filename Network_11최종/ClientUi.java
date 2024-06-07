import javax.swing.*;
import java.awt.*;
import java.util.Vector;

class ClientUi extends JFrame {
    LoginUi ui;
    String id, ip, port;
    // 채팅창 배경설정
    JTextArea textarea = new JTextArea() {
        public void paintComponent(final Graphics g) {
            ImageIcon imageIcon = new ImageIcon("123.jpg");
            Rectangle rect = getVisibleRect();
            g.drawImage(imageIcon.getImage(), rect.x, rect.y, rect.width, rect.height, null);
            setOpaque(false);
            super.paintComponent(g);
        }
    };
    JTextArea idTa; // 채팅창 관련된 버튼 생성
    JTextField topicTf, timeTf, chatTf, nicknameTf;
    JScrollPane sp;
    JPanel tfP, taP, northP, endP, chatP;
    JPanel p1, p2;
    RoundedButton endBtn = new RoundedButton("서버 나가기");
    Container cp;
    Font f = new Font("맑은 고딕", Font.BOLD, 20);
    Font f2 = new Font("맑은 고딕", Font.PLAIN, 20);
    GridBagLayout g = new GridBagLayout();
    GridBagConstraints gc, gc2;
    Color c1, c2;
    Dimension d1;
    Vector<PanelUi> pv = new Vector<>();
    
    // 클라이언트 UI 생성자
    ClientUi(LoginUi ui) {
        try {
            this.ui = ui;
            this.id = ui.id;
            this.ip = ui.ip;
            this.port = String.valueOf(ui.port);
            System.out.println("Cui의: " + ip + port + id);
            init(); // UI 초기화
            setUi(); // UI 설정
            new Client(this); // 액션 리스너 삽입
        } catch (Exception e) {
            e.printStackTrace(); // 예외 발생 시 오류 메시지 출력
        }
    }

    // UI 설정
    void setUi() {
        setVisible(true);
        setTitle(id + "(으)로 채팅중..(ip: " + ip + ", port: " + port + ")");
        setSize(900, 750);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // UI 초기화
    void init() {
        cp = getContentPane();
        cp.setLayout(new BorderLayout()); 

        northP = new JPanel(new BorderLayout());
        tfP = new ImagePanel("pBack.png");
        tfP.setLayout(new FlowLayout(FlowLayout.LEFT)); // 닉네임 필드를 왼쪽으로 정렬

        // 닉네임 라벨과 텍스트 필드 추가
        JLabel nicknameLabel = new JLabel("닉네임 : ");
        nicknameLabel.setFont(f);
        nicknameTf = new JTextField(id, 10);
        nicknameTf.setEnabled(false);
        nicknameTf.setFont(f);
        nicknameTf.setDisabledTextColor(Color.black);

        JPanel nicknamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 닉네임 나오는 필드
        nicknamePanel.add(nicknameLabel);
        nicknamePanel.add(nicknameTf);

        topicTf = new JTextField(10); // 제시어 필드
        topicTf.setEnabled(false);
        topicTf.setFont(f);
        topicTf.setDisabledTextColor(Color.black);

        JPanel topicP = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 제시어 나오는 필드
        topicP.add(topicTf);

        timeTf = new JTextField(10); // 남은 시간 필드
        timeTf.setEnabled(false);
        timeTf.setFont(f);
        timeTf.setDisabledTextColor(Color.black);
        JPanel selectP = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 남은 시간 나오는 필드
        selectP.add(timeTf);

        tfP.add(nicknamePanel); // 닉네임 패널을 tfP에 먼저 추가
        tfP.add(topicP); // topicP를 tfP에 추가
        tfP.add(selectP); // selectP를 tfP에 추가

        northP.add(tfP, BorderLayout.CENTER); 
        cp.add(northP, BorderLayout.NORTH); 

        chatP = new ImagePanel("123.jpg"); // 채팅창 안에 그림 추가하기
        chatTf = new JTextField(30);
        chatTf.setFont(f2);
        chatTf.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        chatP.add(chatTf);
        endP = new JPanel();
        chatP.add(endBtn);
        cp.add(chatP, BorderLayout.SOUTH); 

        p1 = new JPanel(new BorderLayout());
        JLabel lb = new JLabel(new ImageIcon(getClass().getResource("asd.png")));
        // 왼쪽 그림 추가
        p1.add(lb);
        p1.setPreferredSize(new Dimension(150, 600));
        p2 = new JPanel(new BorderLayout());
        p2.setPreferredSize(new Dimension(150, 600));
        lb = new JLabel(new ImageIcon(getClass().getResource("asd.png")));
        p2.add(lb);
        // 오른쪽 그림 추가

        taP = new JPanel(new BorderLayout());
        taP.add(textarea, BorderLayout.CENTER);
        sp = new JScrollPane(textarea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
       
        sp.getVerticalScrollBar().setValue(sp.getVerticalScrollBar().getMaximum());
        taP.add(sp); // textarea 패널

        textarea.setLineWrap(true); // 텍스트가 자동으로 줄 바꿈되도록 설정
        textarea.setEnabled(false);
        textarea.setDisabledTextColor(new Color(200, 150, 100));
        textarea.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        cp.add(taP, BorderLayout.CENTER);
        cp.add(p1, BorderLayout.WEST);
        cp.add(p2, BorderLayout.EAST);
    }
}

class PanelUi {
    JPanel panel;
    JLabel imgLb;
    JLabel idLb;
    Font f = new Font("맑은 고딕", Font.BOLD, 20);
    ClientUi cui;

    // 패널 UI 생성자
    PanelUi(ClientUi cui) {
        this.cui = cui;
        for (int i = 0; i < 8; i++) {
            panel = new JPanel(new BorderLayout());
            imgLb = new JLabel(new ImageIcon("buddy.jpg")); // 플레이어 이미지 라벨
            imgLb.setName(String.valueOf(i) + "imgLb");
            idLb = new JLabel("아이디"); // 아이디 라벨
            idLb.setName(String.valueOf(i) + "idLb");
            idLb.setFont(f);
            idLb.setHorizontalAlignment(0);
            idLb.setForeground(Color.black);
            idLb.setBackground(Color.gray);
            idLb.setOpaque(true);
            panel.add(imgLb);
            panel.add(idLb, BorderLayout.SOUTH);

            if (cui.pv.size() < 4) {
                cui.p1.add(panel);
            } else if (cui.pv.size() >= 4) {
                cui.p2.add(panel);
            }
            cui.pv.add(this);
        }
    }
}

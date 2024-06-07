import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.RoundRectangle2D;
import java.io.*;

import static java.lang.Thread.*;

class LoginUi extends JFrame implements ActionListener, Runnable {

    String id, ip, port;
    RoundedButton serverBtn, clientBTn, endBtn;
    JPanel p1;
    Thread th = new Thread(this);


    public void run() {
        if (currentThread().equals(th)) {
            serverBtn.setVisible(true);
            clientBTn.setVisible(true);
            endBtn.setVisible(true);
        }

    }

    LoginUi() {
        init();
    }

    void init() { //시작할 때 버튼 설정
        serverBtn = new RoundedButton("서버 생성하기");
        clientBTn = new RoundedButton("서버 입장하기");
        endBtn = new RoundedButton("종료하기");
        p1 = new ImagePanel("mainimage1.png");
        setContentPane(p1);
        p1.add(serverBtn);
        p1.add(clientBTn);
        p1.add(endBtn);
        setUi();
        serverBtn.setBounds(320, 340, 150, 40);//버튼위치조정
        serverBtn.setVisible(false);
        clientBTn.setBounds(320, 420, 150, 40);
        clientBTn.setVisible(false);
        endBtn.setBounds(320, 500, 150, 40);
        endBtn.setVisible(false);
        action();
        th.start();
    }

    void setUi() { //기본 화면 세팅
        setTitle("라이어게임");
        setVisible(true);
        setSize(805, 630);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(serverBtn)) {//서버 생성하기 버튼 클릭
            StartDialog lD = new StartDialog(this, this, "서버 생성하기");
            ip = lD.ipTf.getText();
            port = lD.portTf.getText();

        }
        if (e.getSource().equals(clientBTn)) {//서버 입장하기 버튼 클릭
            StartDialog lD = new StartDialog(this, this, "서버 입장하기");
            ip = lD.ipTf.getText();
            port = lD.portTf.getText();
        }
        if (e.getSource().equals(endBtn)) {//종료하기 버튼 클릭
            System.exit(0);
        }
    }

    void action() {
        serverBtn.addActionListener(this);
        clientBTn.addActionListener(this);
        endBtn.addActionListener(this);
    }

    void reopen() {
        setVisible(true);

    }

    public static void main(String[] args) {
        new LoginUi();
    }
}                                                                                           //LoginUi

class ImagePanel extends JPanel {//지정된 이미지 패널 배경으로 표시되는 기능

    Image image;

    public ImagePanel(String str) {                                                  //       패널에 이미지 입또歐

        image = Toolkit.getDefaultToolkit().createImage(str);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
    }

}

class StartDialog extends JDialog implements ActionListener, KeyListener { //    버튼 클릭시 생성되는 창          todo
    JTextField idTf, ipTf, portTf;
    JLabel idLb, ipLb, portLb;
    JButton okBtn, noBtn;
    JPanel p1;
    JFrame frame;
    LoginUi ui;
    String title;

    StartDialog(JFrame frame, LoginUi ui, String title) {
        super(frame, title, true);
        this.frame = frame;
        this.ui = ui;
        this.title = title;
        init();
        if (getTitle().equals("서버 생성하기")) {
            addS();
            setUiS();
        } else if (getTitle().equals("서버 입장하기")) {
            addC();
            setUiC();
        }
    }

    void init() {//서버 생성, 입장할때 입력하는 것들 버튼 디자인 세팅

        idLb = new JLabel("닉네임");
        idLb.setOpaque(true);
        idLb.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        idLb.setBackground(new Color(201, 140, 255));
        idLb.setHorizontalAlignment(JLabel.CENTER);
        idTf = new JTextField(10);

        ipLb = new JLabel("아이피");
        ipLb.setOpaque(true);
        ipLb.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        ipLb.setBackground(new Color(201, 140, 255));
        ipLb.setHorizontalAlignment(JLabel.CENTER);
        ipTf = new JTextField(10);

        portLb = new JLabel("포트");
        portLb.setOpaque(true);
        portLb.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        portLb.setBackground(new Color(201, 140, 255));
        portLb.setHorizontalAlignment(JLabel.CENTER);
        portTf = new JTextField(10);
        portTf.addKeyListener(this);

        okBtn = new JButton("확인");
        okBtn.setBackground(new Color(255,224,140));
        noBtn = new JButton("취소");
        noBtn.setBackground(new Color(255,224,140));
        okBtn.addActionListener(this);
        noBtn.addActionListener(this);
        p1 = new JPanel();
    }

    void setUiS() { //서버 생성하기 눌렀을 때 그리드 생성
        setLayout(new GridLayout(2, 2));
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    void addS() { //서버 생성하기 그리드 안에 들어갈 내용들
        add(portLb);
        add(portTf);
        add(okBtn);
        add(noBtn);
    }
    void setUiC() {//서버 입장하기 눌렀을 때 그리드 생성
        setLayout(new GridLayout(4, 2));
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    void addC() { //서버 입장하기 그리드 안에 들어갈 내용들
        add(idLb);
        add(idTf);
        add(ipLb);
        add(ipTf);
        add(portLb);
        add(portTf);
        add(okBtn);
        add(noBtn);
    }


    public void actionPerformed(ActionEvent e) {

        if (e.getSource().equals(okBtn) & title.equals("서버 생성하기")) {//서버 생성하기 확인을 눌렀을 때 ->포트번호 저장 및 유효한지 확인 후 새로운 ServerUi 인스턴스 생성
            ui.port = portTf.getText().trim();
            Boolean chk = checkP();
            if (chk = true) {
                dispose();
                frame.dispose();
                new ServerUi(ui);
            }
        } else if (e.getSource().equals(okBtn) & title.equals("서버 입장하기")) {// 서버 입장하기 내용 입력 후 확인 눌렀을 때 -> 입력값들 저장, 유효한지 확인, 새로운 ClientUi 인스턴스 생성
            ui.id = idTf.getText().trim();
            ui.ip = ipTf.getText().trim();
            ui.port = portTf.getText().trim();
            Boolean chk = checkP();
            if (chk == true) {
                dispose();
                frame.dispose();
                new ClientUi(ui);
            } else {
                portTf.setText("");
            }
        } else if (e.getSource().equals(noBtn)) {//취소 버튼 클릭시 다 비우기
            idTf.setText("");
            ipTf.setText("");
            portTf.setText("");
            dispose();
        }
    }

    boolean checkP() {//포트번호 유효한지 확인
        try {


            int i = Integer.parseInt(ui.port);
            if (1 > i | i > 65535) {
                JOptionPane.showMessageDialog(null, "정확한 포트를 입력해주세요.");
                return false;
            }
            return true;
        } catch (NumberFormatException ne) {
            JOptionPane.showMessageDialog(null, "정확한 포트를 입력해주세요");
            return false;
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            okBtn.doClick();
        }
    }
}

class RoundedButton extends JButton { //초기 화면 버튼 디자인 세팅                                                    //라운디드 버튼 클래스

    private static final long serialVersionUID = 1L;
    private Color startColor = new Color(252, 210, 87);
    private Color endColor = new Color(255, 251, 0);
    private Color rollOverColor = new Color(255, 143, 89);
    private Color pressedColor = new Color(231, 126, 40);
    private int outerRoundRectSize = 10;
    private int innerRoundRectSize = 8;
    private GradientPaint GP;

    public RoundedButton() {
        this(null, null);
    }

    public RoundedButton(String text) {
        this(text, null);
    }

    public RoundedButton(Action a) {
        this(null, null);
        setAction(a);
    }

    public RoundedButton(Icon icon) {
        this(null, icon);
    }

    public RoundedButton(String text, Icon icon) {
        super(text, icon);

        setContentAreaFilled(false);
        setBorderPainted(false);
        setFont(new Font("맑은 고딕", Font.BOLD, 16));
        setForeground(Color.WHITE);
        setFocusable(false);
    }
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int h = getHeight();
        int w = getWidth();
        ButtonModel model = getModel();

        Color currentColor;
        if (!model.isEnabled()) {
            setForeground(Color.GRAY);
            currentColor = new Color(192, 192, 192);
        } else {
            setForeground(Color.white);
                currentColor = startColor;
        }

        g2d.setPaint(currentColor);
        RoundRectangle2D.Float r2d = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, outerRoundRectSize, outerRoundRectSize);
        Shape clip = g2d.getClip();
        g2d.clip(r2d);
        g2d.fillRect(0, 0, w, h);
        g2d.setClip(clip);

        g2d.dispose();
        super.paintComponent(g);
    }

}

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Result extends JDialog implements ActionListener, Runnable {
    RoundedButton okButton;
    JPanel liarwinPanel, liarlosePanel;
    Thread waitTh;
    String result="";

    //client에서 받아온 결과와 비교, 결과에 맞는 각각의 함수 사용
    Result(Client client ,String string) {
        this.result = string;
        if(result.equals("liarLose")){ //라이어가 패배한경우
            liarLose();
        }
        else if(result.equals("liarWin")){ //라이어가 승리한경우
            liarWin();
        }
    }

    void initUi() { //다이얼로그 초기 설정
        setTitle("결과");
        setSize(380, 185);
        setResizable(false); //사이즈 변경 x
        setLocationRelativeTo(null); //화면의 중앙에 다이얼로그 배치
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);//다이얼로그 닫을때 동작, 자원 해제
        setVisible(true);
    }

    void liarWin(){ //라이어 승리시 화면의 패널 구성
        liarwinPanel = new ImagePanel("liarwin.png");
        liarwinPanel.setLayout(null);
        setContentPane(liarwinPanel); // 다이얼로그 컨텐츠로 설정

        okButton = new RoundedButton("확인"); //결과창의 패널에 확인 버튼 생성
        okButton.setVisible(false);
        liarwinPanel.add(okButton);
        okButton.setBounds(150, 110, 70, 30);

        waitTh=new Thread(this);
        waitTh.start();
        initUi();
    }

    void liarLose(){ //라이어 패배시 화면의 패널 구성
        liarlosePanel = new ImagePanel("citizenwin.png");
        liarlosePanel.setLayout(null);
        setContentPane(liarlosePanel); // 다이얼로그 컨텐츠로 설정

        okButton = new RoundedButton("확인"); //결과창의 패널에 확인 버튼 생성
        okButton.setVisible(false);
        liarlosePanel.add(okButton);
        okButton.setBounds(150, 110, 70, 30);

        waitTh=new Thread(this);
        waitTh.start();
        initUi();
    }

    @Override
    public void run() { //waitTh 스레드의 start()부분
        try {
            Thread.sleep(3000); //결과 확인을 위해,sleep 3초 그이후에 확인 버튼 visible
            okButton.setVisible(true);
            okButton.addActionListener(this);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e){ //완료버튼 누르면 다이얼로그 닫기
        dispose();
    }

}
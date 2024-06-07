
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

class VoteUi extends JDialog implements ActionListener,ListSelectionListener{

    JList<String> PlayerList;
    JFrame frame;
    JPanel VotePanel,ButtonPanel;
    ClientUi clientUi;
    Client client;
    RoundedButton VoteButton = new RoundedButton("투표하기");
    String votedclient;
    private Timer timer;
    private boolean voted = false;


    VoteUi(Client client) { //client의 객체 받아오기
        super(client.cui, "투표하기", true); //dialog 제목을 "투표하기"로 설정
        init(client);
    }

    private void init(Client client) {//받아온 객체를 초기화
        this.clientUi = client.cui;
        this.client = client;
        this.frame = client.frame;
    }

    void initUi(){//투표화면 dialog를 초기화하고 구성하기
        setSize(300, 190);
        setLocationRelativeTo(null); //중앙에 dialog배치
        setBackground(Color.black);
        setVisible(true);
    }

    void initPanel(){//패널을 초기화하고 구성하기
        PlayerList = new JList<>(client.idList); //client목록표시
        PlayerList.setVisibleRowCount(4);
        PlayerList.setSelectedIndex(0);

        //전체 큰 패널을 설정
        VotePanel = new JPanel();
        setContentPane(VotePanel); // dialog의 컨텐츠로 사용하기 위해
        VotePanel.setLayout(new BorderLayout());
        VotePanel.add(PlayerList,BorderLayout.CENTER);
        PlayerList.setBounds(0,0,100,100);

        //버튼을 붙힐 패널, 버튼추가
        ButtonPanel = new JPanel();
        ButtonPanel.add(VoteButton);
        ButtonPanel.setBackground(Color.black);
        VotePanel.add(ButtonPanel,BorderLayout.SOUTH);//큰패널에 작은버튼패널 맨 밑에 붙히기

        //투표화면의 플레이어버튼에 대한 설정
        VoteButton.setPreferredSize(new Dimension(100,30));
        PlayerList.setLayoutOrientation(JList.HORIZONTAL_WRAP); //사용자의 항목을 가로로길게 설정
        PlayerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //단일 선택모드
        PlayerList.addListSelectionListener(this); //항목이 선택된거를 감지하는 리스너
        VoteButton.setActionCommand("투표하기"); // 액션명령
        VoteButton.addActionListener(this); //버튼누르기 이벤트에 대한 리스너
    }

    String getResult() {//투표하거나 시간내에 투표하지 못했을때 결과값 리턴
        initPanel();
        initUi();
        voteTimer();
        
        return votedclient;
    }

    private void voteTimer() {//투표시간동안 타이머재기
        timer = new Timer(10000, e -> {
            if (!voted) {
                // 타이머가 만료되고 아직 투표하지 않았을 때
                dispose(); // 다이얼로그 닫기
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void actionPerformed(ActionEvent e) { //버튼이 눌러지면 실행되는곳
        if (e.getSource().equals(VoteButton)) { //액션명령 = "투표하기"
            int index = PlayerList.getSelectedIndex(); //선택된 인덱스값으로 id찾기
            votedclient = client.idList.get(index);
            voted = true;
        }
        dispose();
    }
    public void valueChanged(ListSelectionEvent e) { //변경된 인덱스값의 클라id가져오기, 사용되지는 않음

    }

}
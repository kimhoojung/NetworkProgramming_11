

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GameManager {

    List<String> players = new ArrayList();
    String topic;
    ArrayList<String> topics;
    String liar;
    Scanner scanner;
    LiarServer ls;
    HashSet<OneClientModul> ocmSet = new HashSet<>();

    GameManager(LiarServer ls) {
        this.ls = ls;
        for (OneClientModul ocm : ls.v) {//서버에서 클라이언트 모듈을 가져와서 플레이어 목록에 추가
            players.add(ocm.chatId);
        }

        // 주제와 라이어를 설정하는 메서드 호출
        SetTopic();
        SetLiar();

        //client에서 liar에게는 liar알려주고 시민에게는 제시어를 알려줌
        for (OneClientModul ocm : ls.v) {
            gm("liar:" + liar);
            gm("topic:" + topic);
        }

        //밑에서 설명
        oneChat();
        voteStart();
        SelectLiar();
        unlockAll();
        ls.sui.startBtn.setEnabled(true); //게임이 끝난 후 liar서버의 시작버튼 활성화
    }

    public void SetTopic() {//주제를 선정하는 메소드

        File topicsFile = new File("주제.txt");//주제가 들어있는 txt파일 가져오기
        try {
            scanner = new Scanner(topicsFile);
            //파일에서 주제를 읽어와서 리스트에 추가
            topics = new ArrayList<>();
            while (scanner.hasNextLine()) {
                topics.add(scanner.nextLine()); //하나씩 읽어와서 topics에 저장
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //랜덤으로 제시어를 선정
        Random randomnumber = new Random();
        int topicsIndex = randomnumber.nextInt(topics.size()); //0~topics.size안의 랜덤 정수 생성
        topic = topics.get(topicsIndex);

    }

    public void SetLiar() { //players중에서 랜덤으로 liar선정
        Random lrandomnumber = new Random();
        int listIndex = lrandomnumber.nextInt(players.size());
        liar = players.get(listIndex);

    }

    //client로 메세지를 보내서 채팅을 금지시킨다.
    void lockChat() {
        gm("채팅락");
    }

    //순서대로 한번씩 말할수 있도록 채팅 잠금
    void oneChat() {
        // 모든 클라이언트에게 채팅 잠금 메시지 전송
        ls.ocm.broadcast("채팅이 잠깁니다.");
        ls.ocm.broadcast("10초 후 순서대로 주제를 한마디로 설명하세요.");
        lockChat();
        ls.sleepTh(10); //10초동안 다 일시정지
        everyonechat();
    }

    void everyonechat() {
        ArrayList<Integer> index = new ArrayList<>();
        Random number = new Random();

        // 랜덤으로 클라이언트를 선택하여 채팅 언락
        //인덱스리스트에 랜덤한 정수를 0부터 하나씩 저장
        for (int i = 0; i < ls.v.size(); i++) {
            int n = number.nextInt(ls.v.size());
            index.add(n);
            for (int j = 0; j < i; j++) { // 중복되지 않도록 전에 저장했던것들을 하나씩 돌면서 중복되면 그 숫자 제거
                if (index.get(j) == index.get(i)) {
                    index.remove(i);
                    i--;
                }
            }
        }
        for (int i = 0; i < ls.v.size(); i++) {//인덱스배열을 하나씩 돌면서 v안의 chatid에게 발언권 부여
            ls.ocm.broadcast(ls.v.get(index.get(i)).chatId+"님이 입력중입니다.");
            gm("채팅언락" + ls.v.get(index.get(i)).chatId); //선택된 클라이언트의 채팅을 활성화
            ls.sleepTh(10); //말하는 10초동안 서버대기
        }

    }

    public void voteStart() {//투표시간 20초동안 투표진행
        //모든 클라이언트에게 투표리스트 전송하도록 client에서 실행
        for (OneClientModul ocm : ls.v) {
            gm("list" + ocm.chatId);
        }

        //client에서 voteUi열도록 실행, 투표시간 20초
        gm("vote");
        ls.sleepTh(20);
    }


    void SelectLiar() { //가장 많은 투표를 받은 사랑을 선정
        int max = 0;
        String votedId = "";
        for (OneClientModul ocm : ls.v) {
            int j = Collections.frequency(ls.voteList, ocm.chatId); // 클라이언트가 받은 투표수를 계산
            if (max < j) { //최다 투표수를 업데이트, 그리고 그 클라이언트id를 votedId에저장
                max = j;
                votedId = ocm.chatId;
            }
        }


        if (liar.equals(votedId)) {//1)라이어가 최다 득표자일때
            gm("votecom" + votedId); // client에서 라이어 제시어 추리 진행
            ls.ocm.broadcast("라이어를 찾았습니다.");
            ls.ocm.broadcast("Liar: " + liar); //라이어 밝히기
            ls.ocm.broadcast("라이어가 제시어를 추리중입니다.");
            ls.sleepTh(10);//라이어가 추리하는 시간(10초)동안 일시정지
            
            String liarAnswer;
            liarAnswer = ls.currentTopic;//서버에서 라이어의 제시어를 받아옴
            if (liarAnswer.equals(topic)) { // 1-1)라이어가 제시어를 맞춘 경우
                ls.ocm.broadcast("라이어승리");
                result("resultliarWin");
            }

            else if (liarAnswer.equals("10초초과")) {// 1-2)시간 초과로 제시어를 입력하지 못한 경우
                ls.ocm.broadcast("라이어가 제한시간에 제시어를 입력하지 못했습니다.");
                ls.ocm.broadcast("라이어패배");
                result("resultliarLose");

            } else {// 1-3)제시어를 틀린 경우
                ls.ocm.broadcast("라이어가 제시어를 맞히지 못햇습니다." +
                        "\n라이어 패배");
                ls.ocm.broadcast(" 제시어 : " + topic + "\n라이어가 입력한 제시어 : " + liarAnswer);
                result("resultliarLose");
            }
            ls.currentTopic = "10초초과";
        } else {//2)라이어를 투표하지 못했을때
            ls.ocm.broadcast("라이어를 찾아내지 못했습니다.\n Liar: " + liar);
            ls.ocm.broadcast("라이어승리");
            result("resultliarWin");
        }
    }

    //client에서 모든 플레이어가 다 채팅할수있도록 실행
    void unlockAll() {
        gm("올언락");
    }

    //GameManager->cilent->result
    void result(String str) {
        gm(str);
        ocmSet.removeAll(ocmSet);//ocmSet 비우기
    }
    void gm(String str) {
        ls.ocm.broadcast("gm" + str);
    }

}
import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class GameManager {
    
    
    String theme; //주제
    ArrayList<String> themelist; //주제 리스트 파일 .txt 을 담을 예정
    String liar; //라이어로 선정된 player

    List<String> playernamelist = new ArrayList(); //플레이어들 명단리스트
    Server server; //라이어서버 변수
    HashSet<OneClientModul> ocmSet = new HashSet<>(); //ocm의 해쉬셋

    GameManager(Server server) {
        this.server = server;   //서버 받아오기
        for (OneClientModul ocm : server.v) {   //사용자 id를 저장
            playernamelist.add(ocm.chatId);
        }

        setTheme();     //라이어와 테마 출력
        setLiar();
        System.out.println(liar);
        System.out.println(theme);
        for (OneClientModul ocm : server.v){
            server.ocm.broadcast("라이어:" + liar);
            server.ocm.broadcast("주제:" + theme);
        }
        vote();
    }

    //주제 선정
    public void setTheme() {
        File file = new File("theme.txt"); //주제 파일 스트림으로 받아오기
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(file);
            br = new BufferedReader(br);
            String line = "";
            int readcount = 0;
            while((line = br.readLine()) != null){
                themelist.add(line);
            }
            br.close();
            fr.close();
        } catch (FileNotFoundException e) {
            System.out.println("파일을 찾을 수 없습니다.");
        }

        Random randomtheme = new Random(); //랜덤주제 선정 
        int index = randomtheme.nextInt(themelist.size()); //0~테마개수 중 한 수 선택
        theme = themelist.get(index); //그 인덱스의 theme를 가져온다.
    }

    //라이어 선정
    public void setLiar() {
        Random ramdomliar = new Random();
        int index = ramdomliar.nextInt(playernamelist.size()); //player의 수 중에서 랜덤 숫자 한개
        liar = playernamelist.get(index);  //playerlist의 인덱스 값을 가져온다

    }

    //투표선언하기
    public void vote() {
        server.ocm.broadcast("투표할 시간이 되었습니다.");
             
    }

}

package app;

//ブロックチェーンのシングルスレッドによるマイニング動作の確認用プログラム(P2P通信の実装はなし)
import java.security.MessageDigest;
import java.util.Date;
import java.util.Random;
import java.util.ArrayList;

//ブロックチェーンクラス
class Blockchain{
    Long nonce;
    Date timestamp;
    String previous_hash;//自身の前のブロックのハッシュ値
    int cash_data;//トランザクションデータ
    String tarnsaction;
    String hash;//自身のハッシュ値
    //difficultyがマイニングの難易度に直結するのでマイニング時間に応じて変更させる(デフォルトは6)
    public Blockchain(String previous_hash,int cash_data,String transaction){
        this.timestamp=new Date();
        this.cash_data=cash_data;
        this.tarnsaction=transaction;
        this.previous_hash=previous_hash;

    }
    //中身を表示する為のメソッド
    public void show_block(){
        System.out.println("作成したブッロクの内容\nprevious hash : "+this.previous_hash+"\ncash data : "+
        this.cash_data+"\ntransaction data :"+this.tarnsaction+"\ntime stamp :"+this.timestamp);
    }
}

//SHA-256(ハッシュ化)を行うクラス
class Sha256{
    public static String sha_256(String plain){
    byte[] cipher_byte;
    try{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
md.update(plain.getBytes());
cipher_byte = md.digest();
StringBuffer sb = new StringBuffer(2 * cipher_byte.length);
 for(byte b: cipher_byte) {
    sb.append(String.format("%02x", b&0xff) );
    }
    return sb.toString();
    }
   catch (Exception e) {
        e.printStackTrace();
} 
return plain;
}
    }
 
//作成したブロックが正しいかどうか確認を行うクラス
class Check_block{
    Blockchain previous_block;
    Blockchain current_block;
public Check_block(Blockchain pre_block,Blockchain block){
this.previous_block=pre_block;
this.current_block=block;
}

public Boolean confirm_block() {
if(!previous_block.hash.equals(current_block.previous_hash))
return false;
if(!current_block.hash.equals(Sha256.sha_256(current_block.previous_hash+current_block.cash_data
+current_block.tarnsaction+current_block.timestamp+current_block.nonce)))
return false;
return true;
}

}
//マイニングを行うクラス
class Mining{
    Random random = new Random();
    String search;
    String tmp_hash;        
    Boolean loop=false;
    long q=0L;
    public int Search_nonce(Blockchain block){
    //マイニング開始前に使用する変数の初期化をする
    loop=false;
    q=0L;
    Long mining= (random.nextInt(20)+1)*1000000000L;//難易度によってminingに渡す値は調整する
    String target = new String(new char[block.difficulty]).replace('\0', '0');
    while(!loop){
        q++;
        try{
    //先頭から0が何bit連続しているのか測定
    tmp_hash=Sha256.sha_256(block.previous_hash+block.cash_data+block.tarnsaction+block.timestamp+mining);
    if(tmp_hash.substring(0,block.difficulty).equals(target)){
            block.nonce=mining;
            block.hash=tmp_hash.toString();
            loop=true;
            System.out.println("探索回数->"+q+"回");
            return 1;
    }

mining++;
    }
catch (Exception e) {
    e.printStackTrace();
    return -1;
}
    }

return 0;
}
}

class Single_thread{
   public static ArrayList<Blockchain> block = new ArrayList<Blockchain>();
    public static void main(String[] args){
        Boolean confirm=false;
        Mining mining_nonce= new Mining();
 
        for(int i=0;i<3;i++){
            Random random = new Random();
             //取引に使用するデータ
        int cash=random.nextInt(20000)+1;
        String transaction;
        //偶数・奇数で処理を分岐
        if(cash%2==0){
            transaction="increase";
        }else{
            transaction="decrease";
        }
            if(block.size()==0)
        block.add(new Blockchain("Genesis_hash",cash,transaction));
            else
        block.add(new Blockchain(block.get(block.size()-1).hash, cash,transaction));
        System.out.println("マイニング作業"+(i+1)+"回目");
        //テキストマイニングの作業
        long startTime = System.currentTimeMillis();
        mining_nonce.Search_nonce(block.get(i)); 
        long endTime = System.currentTimeMillis();
        long min=(endTime-startTime)/60000;
        long sec=((endTime-startTime)-60000*min)/1000;
        System.out.println("処理時間：" + min +" min "+ sec+" sec "+ ((endTime - startTime)-60000*min-1000*sec) + " ms");
        System.out.println("nonce->"+block.get(i).nonce);
        System.out.println("SHA-256->"+block.get(i).hash);
        if(block.size()>1){
            Check_block check = new Check_block(block.get(block.size()-2),block.get(block.size()-1));
           confirm=check.confirm_block(); 
        if(confirm)
        System.out.println("正しいブロックが作成されました");
            else
        System.out.println("間違ったブロックが生成されました");
    }

        block.get(i).show_block();
        }
}
}


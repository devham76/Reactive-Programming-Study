import java.util.Observable;
import java.util.Observer;

public class Main {

    /*
    Reactive streams

   * */

    static class IntObservable extends Observable implements Runnable{  // Runnable : 비동기적으로 동작한다
        // 너가 생성하는 이벤트를 받고싶어

        public void run(){
            for(int i=1; i<10; i++){
                setChanged();
                notifyObservers(i); // data 던지는쪽       // push
                // int i = it.next();                   // pull
            }
        }
    }
    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        Observer ob = new Observer(){
            @Override
            public void update(Observable o, Object arg) { // data 받는쪽
                System.out.println(arg);
            }
        };

        IntObservable io = new IntObservable();
        io.addObserver(ob); // ob가 data를 받기는 하지만, 사실상 데이터를 던지는 쪽은 io의 notifyObservers()이다

        io.run();
    }

}

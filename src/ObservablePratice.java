import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

public class ObservablePratice {

    /*
    Observer 패턴 특징
    1. 데이터 다줬다. 끝이다. 라는 개념이 없다. Complete를 어떻게 할것이냐?가 없다
    2. exception이 발생했을 때 어떻게 처리할것이냐에 대한게 없다

   * */

    // Iterable <---> Observable 상대성 (duality, 기능은 똑같은데, 반대로 표현한것)
    // Pull 방식 <--> Push 방식

    // Observable : Source가 Event/Data를 Observer(관찰자)에게 던진다

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

        Main.IntObservable io = new Main.IntObservable();
        io.addObserver(ob); // ob가 data를 받기는 하지만, 사실상 데이터를 던지는 쪽은 io의 notifyObservers()이다

        io.run();
    }

}

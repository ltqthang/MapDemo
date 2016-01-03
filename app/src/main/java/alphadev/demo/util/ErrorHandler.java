package alphadev.demo.util;

import rx.functions.Action1;

public class ErrorHandler {
    public static Action1<Throwable> logException() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        };
    }
}

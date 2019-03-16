/*
 *    Copyright (C) 2016 Tamic
 *
 *    link :https://github.com/Tamicer/Novate
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package tk.hongbo.network;

import android.content.Context;

import rx.Subscriber;

/**
 * BaseSubscriber
 */
public abstract class BaseSubscriber<T> extends Subscriber<T> {

    protected Context context;

    public BaseSubscriber(Context context) {
        this.context = context;
    }

    public BaseSubscriber() {
    }

    @Override
    public void onError(Throwable e) {
        if (e != null && e.getMessage() != null){
//            LogWraper.v("Novate", e.getMessage());

        } else {
//            LogWraper.v("Novate", "Throwable  || Message == Null");
        }

        if(e instanceof Throwable){
//            LogWraper.e("Novate", "--> e instanceof Throwable");
//            LogWraper.e("Novate", "--> " + e.getCause().toString());
            onError((Throwable)e);
        } else {
//            LogWraper.e("Novate", "e !instanceof Throwable");
            String detail = "";
            if (e.getCause() != null) {
                detail = e.getCause().getMessage();
            }
//            LogWraper.e("Novate", "--> " + detail);
//            onError(NovateException.handleException(e));
        }
        onCompleted();
    }

    @Override
    public void onStart() {
        super.onStart();
//        LogWraper.v("Novate", "-->http is start");
        // todo some common as show loadding  and check netWork is NetworkAvailable
        // if  NetworkAvailable no !   must to call onCompleted
    }

    @Override
    public void onCompleted() {
//        LogWraper.v("Novate", "-->http is Complete");
        // todo some common as  dismiss loadding
    }
}

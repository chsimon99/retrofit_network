package tk.hongbo.network;

import java.util.HashMap;

import retrofit2.Call;


/**
 * Created by imxu on 2019-04-01.
 */

public class CallApiManager<R> {

    private static CallApiManager instance = null;

    private HashMap<Object, Call<R>> map;

    public static CallApiManager get(){
        if(instance == null){
            synchronized (CallApiManager.class){
                if(instance == null){
                    instance = new CallApiManager();
                }
            }
        }
        return instance;
    }

    private CallApiManager(){
        map = new HashMap<>();
    }

    public void add(Object tag , Call<R> bodyCall){
        map.put(tag,bodyCall);
    }

    public void remove(Object tag){
        if(!map.isEmpty()){
            map.remove(tag);
        }
    }

    public void cancel(Object tag){
        if(map.isEmpty()){
            return;
        }
        if(map.get(tag) == null){
            return;
        }
        if(!map.get(tag).isCanceled()){
            map.get(tag).cancel();
            map.remove(tag);
        }
    }
}

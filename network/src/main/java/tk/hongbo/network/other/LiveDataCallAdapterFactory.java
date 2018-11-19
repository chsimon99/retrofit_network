package tk.hongbo.network.other;

import android.arch.lifecycle.LiveData;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import tk.hongbo.network.data.NetRoot;

public class LiveDataCallAdapterFactory extends CallAdapter.Factory {

    private LiveDataCallAdapterFactory() {
    }

    public static LiveDataCallAdapterFactory create() {
        return new LiveDataCallAdapterFactory();
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != LiveData.class) {
            return null;
        }
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalStateException("Response must be parametrized as " +
                    "LiveData<NetRoot> or LiveData<? extends NetRoot>");
        }
        Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
        Class<?> rawObservableType = getRawType(observableType);
        if (rawObservableType == NetRoot.class) {
            if (!(observableType instanceof ParameterizedType)) {
                throw new IllegalStateException("Response must be parametrized as " +
                        "LiveData<Response<NetRoot>> or LiveData<Response<? extends NetRoot>>");
            }
            return new LiveDataCallAdapter<>(observableType);
        }
        return null;
    }
}

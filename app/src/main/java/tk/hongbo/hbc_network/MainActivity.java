package tk.hongbo.hbc_network;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Response;
import tk.hongbo.hbc_network.entity.LastOfferLimitEntity;
import tk.hongbo.network.net.NetCallback;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        test();
    }

    private void test() {
        viewModel.getOfferLimit().observe(this, data -> {
            Log.d("test", data.getMessage());
        });
    }
}

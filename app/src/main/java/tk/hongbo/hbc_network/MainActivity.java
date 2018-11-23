package tk.hongbo.hbc_network;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;

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
            Log.d("test", "Finish");
        });
    }

    public void upload(View view) {
        viewModel.upload(getExternalFilesDir("test") + File.separator + "test.png");
    }

    public void download(View view) {
        String localPath = getExternalFilesDir("testt") + File.separator + "ttt.png";
        viewModel.download("https://www.baidu.com/img/bd_logo1.png", localPath);
    }
}

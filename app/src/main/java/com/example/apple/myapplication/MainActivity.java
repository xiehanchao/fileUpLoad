package com.example.apple.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private List<File> mFiles = new ArrayList<>();
    private File mZipFile;
    private RecyclerView mFilesList;
    private FilesAdapter mAdapter;
    private Set<File> mIsSelect = new HashSet<>();
    private File externalFilesDir;
    private boolean mIsZipFinish = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFilesList = findViewById(R.id.files_list);
        externalFilesDir = getExternalFilesDir(null);
        File[] files = externalFilesDir.listFiles();
        System.out.println(files.length);
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            if ("xlog".equals(suffix) || "zip".equals(suffix)) {
                mFiles.add(file);
            }
        }

        mFilesList.setLayoutManager(new LinearLayoutManager(this));
        mFilesList.setAdapter(mAdapter = new FilesAdapter());

        Button mDoZip = findViewById(R.id.do_zip);
        mDoZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("mIsSelect = " + mIsSelect);
                double random = Math.random();
                mZipFile = new File(externalFilesDir, "随机"+random+".zip");
                TestZIP.zipFiles(mIsSelect, mZipFile);
            }
        });

        TestZIP.setZipFinishListener(new OnZipFinishListener() {
            @Override
            public void onZipFinish(boolean isFinish) {
                MainActivity.this.mIsZipFinish = isFinish;
                if (isFinish){
                    Toast.makeText(MainActivity.this, "压缩完成", Toast.LENGTH_LONG).show();
                    mFiles.clear();
                    externalFilesDir = getExternalFilesDir(null);
                    File[] files = externalFilesDir.listFiles();
                    System.out.println(files.length);
                    for (int i = 0; i < files.length; i++) {
                        File file = files[i];
                        String fileName = file.getName();
                        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                        if ("xlog".equals(suffix) || "zip".equals(suffix)) {
                            mFiles.add(file);
                        }
                    }
                    mIsSelect.clear();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void produce(View view){
        for (int i = 0; i < 10; i++){
            File file = new File(externalFilesDir, "test_"+i+".xlog");
            try {
                file.createNewFile();
                mFiles.add(file);
                mAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void upload(View view) {
        Log.e("XXX", "upload");
        OkHttpUtil okHttpUtil = new OkHttpUtil();
        String url = "http://192.168.150.124:8080/Yaoyan/HelloServlet";
        okHttpUtil.postFile(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.e("XXX", "失败" + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIsSelect.clear();
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                    }
                });
                Log.e("XXX", "成功");
            }
        }, mIsSelect);
    }

    private class FilesAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    MainActivity.this).inflate(R.layout.item_home, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
            holder.tv.setText(mFiles.get(position).getName());
            holder.isCheck.setEnabled(false);
            holder.isCheck.setChecked(false);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    if (holder.isCheck.isChecked()){
                        holder.isCheck.setChecked(false);
                        mIsSelect.remove(mFiles.get(position));
                    } else {
                        holder.isCheck.setChecked(true);
                        mIsSelect.add(mFiles.get(position));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFiles.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        RadioButton isCheck;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.id_num);
            isCheck = itemView.findViewById(R.id.isCheck);
        }
    }
}

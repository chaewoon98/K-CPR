package com.samsung.android.sdk.accessory.example.helloaccessory.kcpr.viewpager;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.samsung.android.sdk.accessory.example.helloaccessory.kcpr.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    private ImageView item_image;
    private TextView item_description;
    private Button item_button;

    ViewPagerData data;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        item_image = itemView.findViewById(R.id.item_image);
        item_description = itemView.findViewById(R.id.item_description);
//        item_button = itemView.findViewById(R.id.item_button);

//        item_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(this, ConsumerActivity.class);
//
//            }
//        });
    }

    public void onBind(ViewPagerData data){
        this.data = data;

        item_image.setImageResource(data.getImage());
        item_description.setText(data.getDescription());

//        //null인경우 skip버튼
//        //null 아닌 경우 start버튼
//        if(data.getButton_text() != null){
//            item_button.setText(data.getButton_text());
//        }
    }
}

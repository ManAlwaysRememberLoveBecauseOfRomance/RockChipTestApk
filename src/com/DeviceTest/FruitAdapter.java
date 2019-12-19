package com.DeviceTest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
public class FruitAdapter extends ArrayAdapter <Fruit>{
    private  int resourceId;
    public FruitAdapter(Context context, int textViewResource, List<Fruit> objectas){
        super(context,textViewResource,objectas);
        resourceId=textViewResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Fruit fruit= (Fruit) getItem(position);
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView fruitImage=(ImageView) view.findViewById(R.id.im1);
         TextView fruitName=(TextView) view.findViewById(R.id.te1);
        fruitImage.setImageResource(fruit.getImageId());
        fruitName.setText(fruit.getName());
        return view;
    }
}
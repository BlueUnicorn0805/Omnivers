package hawaiiappbuilders.omniversapp.carousel;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.R;

public class CarouselVRAdapter extends RecyclerView.Adapter<CarouselVRAdapter.MyViewHolder> implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        itemListener.onItemClicked(position);
    }

    public interface CarouselItemListener {
        void onItemClicked(int position);
    }

    private final LayoutInflater inflater;
    public ArrayList<String> cList;
    private final Context ctx;
    private CarouselItemListener itemListener;

    public CarouselVRAdapter(Context ctx, ArrayList<String> catList, CarouselItemListener listener) {
        this.ctx = ctx;
        inflater = LayoutInflater.from(ctx);
        this.cList = catList;
        this.itemListener = listener;
    }

    @NonNull
    @Override
    public CarouselVRAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_carousel, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CarouselVRAdapter.MyViewHolder holder, int position) {
//        ImageLoader mImageLoader = ImageLoader.getInstance();
//        mImageLoader.init(ImageLoaderConfiguration.createDefault(ctx.getApplicationContext()));
//        DisplayImageOptions mImageOptions = new DisplayImageOptions.Builder()
//                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//                .cacheInMemory(false)
//                .cacheOnDisk(false)
//                .considerExifParams(true)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .build();
        if (getVideImageLink(position) != null) {
//            mImageLoader.displayImage(getVideImageLink(position), holder.imgBanner, mImageOptions);
            Glide.with(ctx)
                    .load(String.format(getVideImageLink(position)))
                    .centerCrop()
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) // You can adjust the target size as per your requirement
                    .fitCenter() // Adjust the scaling as needed
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk caching
                    .skipMemoryCache(true) // Disable memory caching
                    .into(holder.imgBanner);
        }
        holder.imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.onItemClicked(position);
            }
        });
    }

    public String getVideImageLink(int position) {
        String youtubeID = cList.get(position);
        if (TextUtils.isEmpty(youtubeID)) {
            return "";
        } else {
            return String.format("http://img.youtube.com/vi/%s/0.jpg", youtubeID);
        }
    }

    @Override
    public int getItemCount() {
        return cList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imgBanner;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
        }
    }
}

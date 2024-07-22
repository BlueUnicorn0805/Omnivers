package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

import hawaiiappbuilders.omniversapp.R;

public class EventsContentsAdsAdapter extends RecyclerView.Adapter<EventsContentsAdsAdapter.MyViewHolder> implements View.OnClickListener {

    // Image Loader
    ImageLoader mImageLoader;
    DisplayImageOptions mImageOptions;

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        itemListener.onItemClicked(position);
    }

    public interface AdsItemListener {
        void onItemClicked(int position);
    }

    private LayoutInflater inflater;
    private Context ctx;
    private AdsItemListener itemListener;

    private List<String> videoIDs;

    public EventsContentsAdsAdapter(Context ctx, List<String> videoIds, AdsItemListener listener) {
        this.ctx = ctx;
        inflater = LayoutInflater.from(ctx);
        this.videoIDs = videoIds;
        this.itemListener = listener;

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(ctx.getApplicationContext()));
        mImageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .showImageOnFail(R.mipmap.ic_launcher1_foreground)
                .showImageOnLoading(R.mipmap.ic_launcher1_foreground)
                .showImageForEmptyUri(R.mipmap.ic_launcher1_foreground)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.item_event_contents_ads, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        // Display Image
        mImageLoader.displayImage(getVideLink(position), holder.imageView, mImageOptions);

        // Click Action
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    public String getVideLink(int position) {
        return String.format("http://img.youtube.com/vi/%s/0.jpg", videoIDs.get(position));
    }

    @Override
    public int getItemCount() {
        return videoIDs.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        protected ImageView imageView;


        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

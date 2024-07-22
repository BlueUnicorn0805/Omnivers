package hawaiiappbuilders.omniversapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.MenuListActivity;
import hawaiiappbuilders.omniversapp.R;
import hawaiiappbuilders.omniversapp.model.MenuHeader;

public class RecyclerMenuGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        MenuHeaderViewHolder.HeaderViewHolderCallback {

    private LayoutInflater inflater;
    private static final int REST_IMAGE = 0;
    private static final int MENU_HEADER = 1;
    private static final int MENU_ITEM = 2;

    public ArrayList<MenuHeader> headerList;

    private Context ctx;
    private MenuListActivity menuListActivity;

    ImageLoader _imageLoader;
    DisplayImageOptions _imageOptions;

    int extendedPosition = -1;

    public RecyclerMenuGroupAdapter(Context ctx, ArrayList<MenuHeader> headerList) {

        this.ctx = ctx;
        inflater = LayoutInflater.from(ctx);
        this.headerList = headerList;

        this.menuListActivity = (MenuListActivity) ctx;

        // Initialize the ImageLoader
        _imageLoader = ImageLoader.getInstance();
        _imageLoader.init(ImageLoaderConfiguration.createDefault(ctx));
        _imageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
               /* .showImageOnLoading(R.drawable.outdoorseating)
                .showImageOnFail(R.drawable.outdoorseating)
                .showImageForEmptyUri(R.drawable.outdoorseating)*/
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    @Override
    public int getItemCount() {
        return headerList.size();
    }

    @Override
    public int getItemViewType(int position) {

        MenuHeader itemInfo = headerList.get(position);
        if (itemInfo.get_headerType().equals("img")) {
            return REST_IMAGE;
        } else if (itemInfo.get_headerType().equals("group")) {
            return MENU_HEADER;
        } else {
            return MENU_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case REST_IMAGE:
                view = view = inflater.inflate(R.layout.item_menu_restimage, parent, false);
                return new MenuRestImageHolder(view);
            case MENU_HEADER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_menu_header, parent, false);
                return new MenuHeaderViewHolder(view, this);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_menu_header, parent, false);
                return new MenuHeaderViewHolder(view, this);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int itemViewType = getItemViewType(position);
        MenuHeader itemInfo = headerList.get(position);

        if (itemViewType == REST_IMAGE) {
            bindMenuImageViewHolder(holder, itemInfo);
        } else if (itemViewType == MENU_HEADER) {
            bindMenuHeaderViewHolder(holder, position);
        }
    }

    private void bindMenuImageViewHolder(RecyclerView.ViewHolder holder, MenuHeader headerMenu) {
        MenuRestImageHolder headerViewHolder = (MenuRestImageHolder) holder;

        String videoUrlFormat = "http://img.youtube.com/vi/" + headerMenu.get_headerTitle() + "/0.jpg";
        _imageLoader.displayImage(videoUrlFormat, headerViewHolder.menuRestImage, _imageOptions);
    }

    private void bindMenuHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        MenuHeaderViewHolder headerViewHolder = (MenuHeaderViewHolder) holder;
        MenuHeader menuHeader = headerList.get(position);
        headerViewHolder.MenuHeader.setText(menuHeader.get_headerTitle());
        headerViewHolder.rcvItems.setAdapter(new RecyclerMenuItemAdapter(ctx, menuHeader.getMenuList()));

        if (isExpanded(position)) {
            headerViewHolder.MenuHeader
                    .setCompoundDrawablesWithIntrinsicBounds(null, null, headerViewHolder.arrowUp, null);
            headerViewHolder.rcvItems.setVisibility(View.VISIBLE);
        } else {
            headerViewHolder.MenuHeader
                    .setCompoundDrawablesWithIntrinsicBounds(null, null, headerViewHolder.arrowDown, null);
            headerViewHolder.rcvItems.setVisibility(View.GONE);
        }
    }

    @Override
    public void onHeaderClick(int position) {

        if (extendedPosition == position) {
            extendedPosition = 0;
        } else {
            extendedPosition = position;
        }

        notifyDataSetChanged();
    }

    @Override
    public boolean isExpanded(int position) {
        return extendedPosition == position;
    }
}

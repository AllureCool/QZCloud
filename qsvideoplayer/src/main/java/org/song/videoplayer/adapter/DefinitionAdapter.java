package org.song.videoplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.song.videoplayer.R;
import org.song.videoplayer.SuperVideoView;
import org.song.videoplayer.bean.DefinitionBean;

import java.util.ArrayList;
import java.util.List;

public class DefinitionAdapter extends RecyclerView.Adapter<DefinitionAdapter.DefinitionVHolder> {

    private Context mContext;
    private int mSelectPosition;

    private OnItemClickListener mOnItemClickListener;

    private List<DefinitionBean> mDefinitionList;

    public DefinitionAdapter(Context context) {
        mContext = context;
        mDefinitionList = new ArrayList<>();
    }

    public void setDatas(List<DefinitionBean> definitionList) {
        mDefinitionList.clear();
        mDefinitionList.addAll(definitionList);
        notifyDataSetChanged();
    }

    public void setSelectPosition(int selectPosition) {
        mSelectPosition = selectPosition;
        notifyDataSetChanged();
    }

    @Override
    public DefinitionVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_definition, parent, false);
        return new DefinitionVHolder(view);
    }

    @Override
    public void onBindViewHolder(final DefinitionVHolder holder, int position) {
        final DefinitionBean data = mDefinitionList.get(position);
        if(position == mSelectPosition) {
            holder.ivChoose.setVisibility(View.VISIBLE);
        } else {
            holder.ivChoose.setVisibility(View.INVISIBLE);
        }
        holder.tvDefinition.setText(data.getDefinition());
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int layoutPosition = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, layoutPosition, data);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDefinitionList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, DefinitionBean data);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    class DefinitionVHolder extends RecyclerView.ViewHolder {
        public TextView tvDefinition;
        public ImageView ivChoose;
        public DefinitionVHolder(View view) {
            super(view);
            tvDefinition = (TextView) view.findViewById(R.id.tv_definition);
            ivChoose = (ImageView) view.findViewById(R.id.iv_choose);
        }
    }
}

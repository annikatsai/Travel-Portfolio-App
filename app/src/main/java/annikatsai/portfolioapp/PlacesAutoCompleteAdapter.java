package annikatsai.portfolioapp;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;


public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    ArrayList<String> resultList;

    Context mContext;
    int mResource;

    PlaceAPI mPlaceAPI = new PlaceAPI();

    public PlacesAutoCompleteAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        mResource = resource;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int position) {
        return resultList.get(position);
    }


    // Adding the "powered by Google logo"
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view;
//
//        //if (convertView == null) {
//        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        if (position != (resultList.size() - 1))
//            view = inflater.inflate(R.layout.autocomplete_list_item, null);
//        else
//            view = inflater.inflate(R.layout.autocomplete_google_logo, null);
//        //}
//        //else {
//        //    view = convertView;
//        //}
//
//        if (position != (resultList.size() - 1)) {
//            TextView autocompleteTextView = (TextView) view.findViewById(R.id.autocompleteText);
//            autocompleteTextView.setText(resultList.get(position));
//        }
//        else {
//            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
//            // not sure what to do 😀
//        }
//
//        return view;
//    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if (charSequence != null) {
                    resultList = mPlaceAPI.autocomplete(charSequence.toString());
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults != null && filterResults.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}
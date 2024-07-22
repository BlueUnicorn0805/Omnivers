package hawaiiappbuilders.omniversapp.messaging;

import com.android.volley.VolleyError;

import java.util.ArrayList;

import hawaiiappbuilders.omniversapp.model.FCMTokenData;

public interface OnGetTokenListener {
   void onSuccess(String response);

   void onVolleyError(VolleyError error);

   void onEmptyResponse();

   void onFinishPopulateTokenList(ArrayList<FCMTokenData> tokenList);

   void onJsonArrayEmpty();

   void onJsonException();

   void onTokenListEmpty();
}
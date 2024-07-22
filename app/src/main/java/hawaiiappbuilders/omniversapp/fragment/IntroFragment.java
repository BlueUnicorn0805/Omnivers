/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package hawaiiappbuilders.omniversapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

public class IntroFragment extends BaseFragment implements View.OnClickListener {

    public static IntroFragment newInstance(int layoutId) {
        IntroFragment mFragment = new IntroFragment();
        Bundle mBundle = new Bundle();
        mBundle.putInt(DATA_FRAGMENT, layoutId);
        mFragment.setArguments(mBundle);

        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle data = getArguments();
        int layoutId = data.getInt(DATA_FRAGMENT);
        View view = inflater.inflate(layoutId, container, false);

        init(savedInstanceState);
        initLayout(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initLayout(View parentView) {
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
    }
}

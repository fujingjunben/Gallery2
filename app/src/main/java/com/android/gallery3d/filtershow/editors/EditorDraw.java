/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gallery3d.filtershow.editors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SeekBar;

import com.android.gallery3d.R;
import com.android.gallery3d.filtershow.FilterShowActivity;
import com.android.gallery3d.filtershow.controller.BitmapCaller;
import com.android.gallery3d.filtershow.controller.ColorChooser;
import com.android.gallery3d.filtershow.controller.DrawableCaller;
import com.android.gallery3d.filtershow.controller.FilterView;
import com.android.gallery3d.filtershow.filters.FilterDrawRepresentation;
import com.android.gallery3d.filtershow.filters.FilterRepresentation;
import com.android.gallery3d.filtershow.filters.ImageFilterDraw;
import com.android.gallery3d.filtershow.imageshow.ImageDraw;

public class EditorDraw extends ParametricEditor implements FilterView {
    private static final String LOGTAG = "EditorDraw";
    public static final int ID = R.id.editorDraw;
    public ImageDraw mImageDraw;
    private static final int MODE_SIZE = FilterDrawRepresentation.PARAM_SIZE;
    private static final int MODE_SIZEE = FilterDrawRepresentation.PARAM_SIZE;
    private static final int MODE_STYLE = FilterDrawRepresentation.PARAM_STYLE;
    private static final int MODE_COLOR = FilterDrawRepresentation.PARAM_COLOR;
    int[] brushIcons = {
            R.drawable.filtershow_style_curve,
            R.drawable.filtershow_style_arrow,
            R.drawable.filtershow_style_line,
            R.drawable.filtershow_style_rectangle,
            R.drawable.filtershow_style_circle,
    };

    int[] mBasColors = {
            FilterDrawRepresentation.DEFAULT_MENU_COLOR1,
            FilterDrawRepresentation.DEFAULT_MENU_COLOR2,
            FilterDrawRepresentation.DEFAULT_MENU_COLOR3,
            FilterDrawRepresentation.DEFAULT_MENU_COLOR4,
            FilterDrawRepresentation.DEFAULT_MENU_COLOR5,
    };

    int[] options = {
            R.id.draw_menu_clear,
            R.id.draw_menu_size,
            R.id.draw_menu_style,
            R.id.draw_menu_color,
            R.id.draw_menu_eraser,
    };
    private EditorDrawTabletUI mTabletUI;
    private String mParameterString;
    private int mSelectedColorButton;
    private String mDrawString = null;

    public EditorDraw() {
        super(ID);
    }

    @Override
    public String calculateUserMessage(Context context, String effectName, Object parameterValue) {
        FilterDrawRepresentation rep = getDrawRep();
        if (mDrawString != null) {
            mImageDraw.displayDrawLook();
            return mDrawString;
        }
        if (rep == null) {
            return "";
        }
        if (!ParametricEditor.useCompact(mContext)) {

        }
        if (mParameterString == null) {
            mParameterString = "";
        }
        String paramString;
        String val = rep.getValueString();

        mImageDraw.displayDrawLook();
        return mParameterString + val;
    }

    @Override
    public void createEditor(Context context, FrameLayout frameLayout) {
        mView = mImageShow = mImageDraw = new ImageDraw(context);
        super.createEditor(context, frameLayout);
        mImageDraw.setEditor(this);

    }

    @Override
    public void reflectCurrentFilter() {
        super.reflectCurrentFilter();
        FilterRepresentation rep = getLocalRepresentation();
        if (rep != null && getLocalRepresentation() instanceof FilterDrawRepresentation) {
            FilterDrawRepresentation drawRep = (FilterDrawRepresentation) getLocalRepresentation();
            mImageDraw.setFilterDrawRepresentation(drawRep);
            if (!ParametricEditor.useCompact(mContext)) {
                if (mTabletUI != null) {

                    mTabletUI.setDrawRepresentation(drawRep);
                }
                return;
            }

            drawRep.getParam(FilterDrawRepresentation.PARAM_STYLE).setFilterView(this);
            drawRep.setPramMode(FilterDrawRepresentation.PARAM_COLOR);
            mParameterString = mContext.getString(R.string.draw_color);
            control(drawRep.getCurrentParam(), mEditControl);
        }
    }

    @Override
    public void openUtilityPanel(final LinearLayout accessoryViewList, final LinearLayout listOptions) {
        for (int id : options) {
            final Button button = (Button) listOptions.findViewById(id);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectButton(button);
                }
            });
        }
    }

    @SuppressLint("NonConstantResourceId")
    protected void selectButton(Button button) {
        ImageFilterDraw filter = (ImageFilterDraw) mImageShow.getCurrentFilter();
        FilterDrawRepresentation rep = getDrawRep();
        if (rep == null) {
            return;
        }

        switch (button.getId()) {
            case R.id.draw_menu_clear:
                clearDrawing();
                break;
            case R.id.draw_menu_size:
                rep.setPramMode(FilterDrawRepresentation.PARAM_SIZE);
                break;
            case R.id.draw_menu_style:
                rep.setPramMode(FilterDrawRepresentation.PARAM_STYLE);
                break;
            case R.id.draw_menu_color:
                rep.setPramMode(FilterDrawRepresentation.PARAM_COLOR);
                break;
            case R.id.draw_menu_eraser:
                rep.setPramMode(FilterDrawRepresentation.PARAM_ERASER);
                break;
        }
        if (button.getId() != R.id.draw_menu_clear) {
            mParameterString = button.getText().toString();
            updateText();
        }
        // 如果当前控制器是颜色选择，就拿到当前选择的颜色
        if (mControl instanceof ColorChooser) {
            ColorChooser c = (ColorChooser) mControl;
            mBasColors = c.getColorSet();
        }
        control(rep.getCurrentParam(), mEditControl);
        if (mControl instanceof ColorChooser) {
            ColorChooser c = (ColorChooser) mControl;
            c.setColorSet(mBasColors);
        }
        mControl.updateUI();
        mView.invalidate();
    }

    @Override
    public boolean showsSeekBar() {
        return false;
    }

    public void clearDrawing() {
        ImageDraw idraw = (ImageDraw) mImageShow;
        idraw.resetParameter();
        commitLocalRepresentation();
    }

    @Override
    public void setUtilityPanelUI(View actionButton, View editControl) {
        if (ParametricEditor.useCompact(mContext)) {
            super.setUtilityPanelUI(actionButton, editControl);
            return;
        }

        mSeekBar = (SeekBar) editControl.findViewById(R.id.primarySeekBar);
        if (mSeekBar != null) {
            mSeekBar.setVisibility(View.GONE);
        }
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout lp = (LinearLayout) inflater.inflate(
                R.layout.filtershow_draw_ui, (ViewGroup) editControl, true);

        mTabletUI = new EditorDrawTabletUI(this, mContext, lp);
        mDrawString = mContext.getResources().getString(R.string.imageDraw).toUpperCase();
        setMenuIcon(true);

    }

    FilterDrawRepresentation getDrawRep() {
        FilterRepresentation rep = getLocalRepresentation();
        if (rep instanceof FilterDrawRepresentation) {
            return (FilterDrawRepresentation) rep;
        }
        return null;
    }

    @Override
    public void computeIcon(int index, BitmapCaller caller) {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), brushIcons[index]);
        caller.available(bitmap);
    }

    @Override
    public void computeIcon(int index, DrawableCaller caller) {
        Drawable drawable= mContext.getResources().getDrawable(brushIcons[index]);
        caller.available(drawable);
    }

    public int getBrushIcon(int type) {
        return brushIcons[type];
    }

}

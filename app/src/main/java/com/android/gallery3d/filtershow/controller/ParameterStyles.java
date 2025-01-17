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

package com.android.gallery3d.filtershow.controller;

import android.content.Context;

public interface ParameterStyles extends Parameter {
    public static String sParameterType = "ParameterStyles";

    int getNumberOfStyles();

    int getDefaultSelected();

    int getSelected();

    void setSelected(int value);

    void getIcon(int index, BitmapCaller caller);
    void getIcon(int index, DrawableCaller caller);

    String getStyleTitle(int index, Context context);
}

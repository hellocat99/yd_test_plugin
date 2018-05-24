/*
 * Copyright (c) 2018. Kunlin Wang (http://wangkunlin.date)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yd.test.plugin.util;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * on 2018-05-23.
 */
public class IHorizontalBox extends JPanel {

    private final JPanel myBox;

    public IHorizontalBox() {
        this.setLayout(new BorderLayout());
        this.myBox = new JPanel();
        myBox.setLayout(new BorderLayout());
        super.add(myBox, "West");
        setOpaque(false);
    }

    public Component add(Component aComponent) {
        return myBox.add(aComponent);
    }

    public void remove(Component aComponent) {
        myBox.remove(aComponent);
    }

    public void removeAll() {
        myBox.removeAll();
    }

    @Override
    public void add(@NotNull Component comp, Object constraints) {
        myBox.add(comp, constraints);
    }
}

/**
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.impl.BeanRepositoryImpl;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.server.impl.gc.GarbageCollection;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.Dolphin;

/**
 * Created by hendrikebbers on 09.05.16.
 */
public class ServerBeanRepositoryImpl extends BeanRepositoryImpl implements ServerBeanRepository{

    final GarbageCollection garbageCollection;

    public ServerBeanRepositoryImpl(final Dolphin dolphin, final EventDispatcher dispatcher, final GarbageCollection garbageCollection) {
        super(dolphin, dispatcher);
        this.garbageCollection = Assert.requireNonNull(garbageCollection, "garbageCollection");
    }

    @Override
    public <T> void delete(T bean) {
        super.delete(bean);
        garbageCollection.onBeanRemoved(bean);
    }

    @Override
    public <T> void deleteByGC(T bean) {
        super.delete(bean);
    }
}

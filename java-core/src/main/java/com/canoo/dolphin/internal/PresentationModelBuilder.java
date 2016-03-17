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
package com.canoo.dolphin.internal;

import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.Tag;

/**
 * Created by hendrikebbers on 25.09.15.
 */
public interface PresentationModelBuilder<T extends PresentationModel> {

    PresentationModelBuilder<T> withType(String type);

    PresentationModelBuilder<T> withId(String id);

    PresentationModelBuilder withAttribute(String name);

    PresentationModelBuilder withAttribute(String name, Object value);

    PresentationModelBuilder withAttribute(String name, Object value, Tag tag);

    PresentationModelBuilder withAttribute(String name, Object value, String qualifier);

    PresentationModelBuilder withAttribute(String name, Object value, String qualifier, Tag tag);

    T create();
}

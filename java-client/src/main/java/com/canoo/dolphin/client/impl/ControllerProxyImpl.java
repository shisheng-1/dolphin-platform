/*
 * Copyright 2015 Canoo Engineering AG.
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
package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerActionException;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.impl.ClassRepositoryImpl;
import com.canoo.dolphin.impl.ControllerActionCallBean;
import com.canoo.dolphin.impl.ControllerActionCallErrorBean;
import com.canoo.dolphin.impl.ControllerActionCallParamBean;
import com.canoo.dolphin.impl.ControllerDestroyBean;
import com.canoo.dolphin.impl.DolphinUtils;
import com.canoo.dolphin.impl.PlatformConstants;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.PlatformBeanRepository;
import org.opendolphin.StringUtil;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.canoo.dolphin.impl.ClassRepositoryImpl.FieldType.DOLPHIN_BEAN;

public class ControllerProxyImpl<T> implements ControllerProxy<T> {

    private final String controllerId;

    private final ClientContext context;

    private final BeanRepository beanRepository;

    private final ClientDolphin dolphin;

    private final PlatformBeanRepository platformBeanRepository;

    private T model;

    private volatile boolean destroyed = false;

    public ControllerProxyImpl(String controllerId, T model, ClientContext context, ClientDolphin dolphin, BeanRepository beanRepository, PlatformBeanRepository platformBeanRepository) {
        if (StringUtil.isBlank(controllerId)) {
            throw new NullPointerException("controllerId must not be null");
        }
        if (dolphin == null) {
            throw new NullPointerException("dolphin must not be null");
        }
        if (context == null) {
            throw new NullPointerException("context must not be null");
        }
        if (beanRepository == null) {
            throw new NullPointerException("beanRepository must not be null");
        }
        if (platformBeanRepository == null) {
            throw new NullPointerException("platformBeanRepository must not be null");
        }
        this.beanRepository = beanRepository;
        this.dolphin = dolphin;
        this.controllerId = controllerId;
        this.model = model;
        this.context = context;
        this.platformBeanRepository = platformBeanRepository;
    }

    @Override
    public T getModel() {
        return model;
    }

    @Override
    public CompletableFuture<Void> invoke(String actionName, Param... params) {
        if (destroyed) {
            throw new IllegalStateException("The controller was already destroyed");
        }

        if (params != null && params.length > 0) {
            for (final Param param : params) {
                ControllerActionCallParamBean paramBean = context.getBeanManager().create(ControllerActionCallParamBean.class);
                final ClassRepositoryImpl.FieldType type = DolphinUtils.getFieldType(param.getValue());
                final Object value = type == DOLPHIN_BEAN ? beanRepository.getDolphinId(param.getValue()) : param.getValue();
                paramBean.setValue(value);
                paramBean.setValueType(DolphinUtils.mapFieldTypeToDolphin(type));
            }
        }

        final ControllerActionCallBean bean = platformBeanRepository.getControllerActionCallBean();
        bean.setControllerId(controllerId);
        bean.setActionName(actionName);


        final CompletableFuture<Void> result = new CompletableFuture<>();
        dolphin.send(PlatformConstants.CALL_CONTROLLER_ACTION_COMMAND_NAME, new OnFinishedHandler() {
            @Override
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                final ControllerActionCallErrorBean errorBean = platformBeanRepository.getControllerActionCallErrorBean();
                if (controllerId.equals(errorBean.getControllerId()) && actionName.equals(errorBean.getActionName())) {
                    result.completeExceptionally(new ControllerActionException());
                } else {
                    result.complete(null);
                }
            }

            @Override
            public void onFinishedData(List<Map> data) {
                //Unused....
            }
        });
        return result;
    }

    @Override
    public CompletableFuture<Void> destroy() {
        if (destroyed) {
            throw new IllegalStateException("The controller was already destroyed");
        }
        destroyed = true;
        ControllerDestroyBean bean = context.getBeanManager().findAll(ControllerDestroyBean.class).get(0);
        bean.setControllerId(controllerId);

        final CompletableFuture<Void> ret = new CompletableFuture<>();

        dolphin.send(PlatformConstants.DESTROY_CONTROLLER_COMMAND_NAME, new OnFinishedHandler() {
            @Override
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                model = null;
                ret.complete(null);
            }

            @Override
            public void onFinishedData(List<Map> data) {
                //Unused....
            }
        });
        return ret;
    }
}

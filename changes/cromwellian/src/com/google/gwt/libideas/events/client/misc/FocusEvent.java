/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.libideas.events.client.misc;

import com.google.gwt.libideas.events.client.AbstractEvent;
import com.google.gwt.libideas.events.client.BrowserEvent;
import com.google.gwt.libideas.events.client.BrowserEvents;
import com.google.gwt.user.client.Event;

public class FocusEvent extends BrowserEvent<FocusHandler> {

  public static Key<FocusHandler> KEY = new Key<FocusHandler>(
      BrowserEvents.ONFOCUS);

  protected FocusEvent(Event e) {
    super(e);
  }

  protected void fireEvent(FocusHandler handler) {
    handler.onFocus(this);
  }

  protected AbstractEvent.Key getKey() {
    return KEY;
  }
}
/**
 * Copyright 2011 multibit.org
 *
 * <p>Licensed under the MIT license (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * <p>http://opensource.org/licenses/mit-license.php
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.multibit.platform.listener;

import java.util.Collection;

/**
 * Generic interface to provide the following to generic event handlers:
 *
 * <ul>
 *   <li>Type safety for adding and removing listeners
 * </ul>
 *
 * <p>See {@link org.multibit.platform.handler.DefaultOpenURIHandler} for an example of an
 * implementation
 *
 * @since 0.3.0
 */
public interface GenericEventListener<T> {
  /** @param listeners The listeners to add to the handler */
  void addListeners(Collection<T> listeners);
}

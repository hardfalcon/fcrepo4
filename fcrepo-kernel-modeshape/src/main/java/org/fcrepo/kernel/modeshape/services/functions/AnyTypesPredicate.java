/*
 * Licensed to DuraSpace under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * DuraSpace licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fcrepo.kernel.modeshape.services.functions;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.fcrepo.kernel.modeshape.FedoraJcrConstants.FROZEN_MIXIN_TYPES;
import static org.fcrepo.kernel.modeshape.services.functions.JcrPropertyFunctions.isFrozen;
import static org.fcrepo.kernel.modeshape.services.functions.JcrPropertyFunctions.property2values;
import static org.fcrepo.kernel.modeshape.utils.UncheckedFunction.uncheck;
import static org.fcrepo.kernel.modeshape.utils.UncheckedPredicate.uncheck;

import java.util.Collection;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.fcrepo.kernel.modeshape.utils.UncheckedPredicate;


/**
 * Predicate to match nodes with any of the given mixin types
 * @author armintor@gmail.com
 * @author ajs6f
 *
 */
public class AnyTypesPredicate implements UncheckedPredicate<Node> {
    protected final Collection<String> nodeTypes;

    /**
     * True if any of the types specified match.
     * @param types the types
     */
    public AnyTypesPredicate(final String...types) {
        nodeTypes = asList(types);
    }

    @Override
    public boolean testThrows(final Node input) throws RepositoryException {
        requireNonNull(input, "null node has no types!");
        if (isFrozen.test(input) && input.hasProperty(FROZEN_MIXIN_TYPES)) {
            if (property2values.apply(input.getProperty(FROZEN_MIXIN_TYPES)).map(uncheck(Value::getString))
                    .anyMatch(nodeTypes::contains)) {
                return true;
            }
        }
        return nodeTypes.stream().anyMatch(uncheck(input::isNodeType));
    }
}

/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.smithy.model.selector;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;

/**
 * Selector attribute values are the data model of selectors.
 */
public interface AttributeValue {

    /**
     * Returns the string version of an attribute value.
     *
     * @return Returns the string representation or an empty string.
     */
    @Override
    String toString();

    /**
     * Returns a string representation of the value that can be used
     * for creating formatted messages.
     *
     * <p>This implementation gets the value of {@link #toString()}
     * by default.
     *
     * @return Returns the message string.
     */
    default String toMessageString() {
        return toString();
    }

    /**
     * Gets a property from the attribute value.
     *
     * <p>This method never returns null. It should instead return
     * a null value object when the property does not exist.
     *
     * @param key Property to get.
     * @return Returns the nested property.
     */
    AttributeValue getProperty(String key);

    /**
     * Gets a property using a path to the property.
     *
     * @param path The path to select from the value.
     * @return Returns the created attribute value.
     */
    default AttributeValue getPath(List<String> path) {
        if (!isPresent() || path.isEmpty()) {
            return this;
        }

        AttributeValue current = this;
        for (String segment : path) {
            current = current.getProperty(segment);
            if (!current.isPresent()) {
                break;
            }
        }

        return current;
    }

    /**
     * Checks if the attribute value is considered present.
     *
     * <p>Attribute value are considered present if they are not null. If the
     * attribute value is a projection, then it is considered present if it is
     * not empty.
     *
     * @return Returns true if present.
     */
    default boolean isPresent() {
        return true;
    }

    /**
     * Gets all of the attribute values contained in the attribute value.
     *
     * <p>This will yield a single result for normal attributes, or a list
     * of multiple values for projections.
     *
     * @return Returns the flattened attribute values contained in the attribute value.
     */
    default Collection<? extends AttributeValue> getFlattenedValues() {
        return Collections.singleton(this);
    }

    /**
     * Creates a 'shape' {@code AttributeValue} for the given shape.
     *
     * @param shape Shape to path into.
     * @param vars Variables accessible to the shape.
     * @return Returns the created selector value.
     */
    static AttributeValue shape(Shape shape, Map<String, Set<Shape>> vars) {
        return new AttributeValueImpl.ShapeValue(shape, vars);
    }

    /**
     * Creates an 'id' {@code AttributeValue} from a shape ID.
     *
     * @param id Shape ID to create.
     * @return Returns the created selector value.
     */
    static AttributeValue id(ShapeId id) {
        return new AttributeValueImpl.Id(id);
    }

    /**
     * Creates a 'service' {@code AttributeValue} from a service shape.
     *
     * @param service Shape to create.
     * @return Returns the created selector value.
     */
    static AttributeValue service(ServiceShape service) {
        return new AttributeValueImpl.Service(service);
    }

    /**
     * Creates an empty {@code AttributeValue} object.
     *
     * @return Returns the created selector value.
     */
    static AttributeValue emptyValue() {
        return AttributeValueImpl.EMPTY;
    }

    /**
     * Creates an {@code AttributeValue} that contains a literal value.
     *
     * @param literal Literal value to wrap.
     * @return Returns the created selector value.
     */
    static AttributeValue literal(Object literal) {
        return new AttributeValueImpl.Literal(literal);
    }

    /**
     * Creates a 'node' {@code AttributeValue} for a {@link Node}.
     *
     * @param node Node to create the value from.
     * @return Returns the created attribute value.
     */
    static AttributeValue node(Node node) {
        return new AttributeValueImpl.NodeValue(node);
    }

    /**
     * Creates a 'projection' {@code AttributeValue}.
     *
     * @param values Values stored in the projection.
     * @return Returns the created projection.
     */
    static AttributeValue projection(Collection<AttributeValue> values) {
        return new AttributeValueImpl.Projection(values);
    }
}

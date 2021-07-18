package org.incendo.interfaces.paper.type;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.interfaces.core.Interface;
import org.incendo.interfaces.core.UpdatingInterface;
import org.incendo.interfaces.core.arguments.HashMapInterfaceArguments;
import org.incendo.interfaces.core.arguments.InterfaceArguments;
import org.incendo.interfaces.core.click.ClickHandler;
import org.incendo.interfaces.core.transform.InterfaceProperty;
import org.incendo.interfaces.core.transform.Transform;
import org.incendo.interfaces.core.transform.TransformContext;
import org.incendo.interfaces.core.view.InterfaceView;
import org.incendo.interfaces.paper.PlayerViewer;
import org.incendo.interfaces.paper.click.InventoryClickContext;
import org.incendo.interfaces.paper.pane.ChestPane;
import org.incendo.interfaces.paper.view.ChestView;
import org.incendo.interfaces.paper.view.PlayerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An interface using a chest.
 */
public final class ChestInterface implements
        TitledInterface<ChestPane, PlayerViewer>,
        UpdatingInterface,
        Clickable<ChestPane, InventoryClickEvent, PlayerViewer> {

    private final int rows;
    private final @NonNull List<TransformContext<?, ChestPane, PlayerViewer>> transformationList;
    private final @NonNull List<CloseHandler<ChestPane>> closeHandlerList;
    private final @NonNull Component title;
    private final boolean updates;
    private final int updateDelay;
    private final @NonNull ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer, InventoryClickContext<ChestPane,
            ChestView>> clickHandler;

    /**
     * Constructs {@code ChestInterface}.
     *
     * @param rows          the rows
     * @param title         the interfaces title
     * @param transforms    the transformations to apply
     * @param closeHandlers the close handlers to apply
     * @param updates       {@code true} if the interface is an updating interface
     * @param updateDelay   the update delay
     * @param clickHandler  the handler to run on click
     */
    public ChestInterface(
            final int rows,
            final @NonNull Component title,
            final @NonNull List<TransformContext<?, ChestPane, PlayerViewer>> transforms,
            final @NonNull List<CloseHandler<ChestPane>> closeHandlers,
            final boolean updates,
            final int updateDelay,
            final @NonNull ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer, InventoryClickContext<ChestPane,
                    ChestView>> clickHandler
    ) {
        this.title = title;
        this.transformationList = transforms;
        this.closeHandlerList = closeHandlers;
        this.updates = updates;
        this.updateDelay = updateDelay;
        this.rows = rows;
        this.clickHandler = clickHandler;
    }

    /**
     * Returns a new ChestInterface builder.
     *
     * @return the builder
     */
    public static @NonNull Builder builder() {
        return new ChestInterface.Builder();
    }

    /**
     * Returns the amount of rows.
     *
     * @return the rows
     */
    public int rows() {
        return this.rows;
    }

    @Override
    public @NonNull ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
            InventoryClickContext<ChestPane, ChestView>> clickHandler() {
        return this.clickHandler;
    }

    @Override
    public @NonNull ChestInterface transform(final @NonNull Transform<ChestPane, PlayerViewer> transform) {
        this.transformationList.add(
                TransformContext.of(
                        InterfaceProperty.dummy(),
                        1,
                        transform
                )
        );
        return this;
    }

    @Override
    public @NonNull List<TransformContext<?, ChestPane, PlayerViewer>> transformations() {
        return List.copyOf(this.transformationList);
    }

    /**
     * Returns the list of close handlers.
     *
     * @return the close handlers
     */
    public @NonNull List<CloseHandler<ChestPane>> closeHandlers() {
        return List.copyOf(this.closeHandlerList);
    }

    @Override
    public @NonNull ChestView open(final @NonNull PlayerViewer viewer) {
        return this.open(viewer, HashMapInterfaceArguments.empty());
    }

    @Override
    public @NonNull ChestView open(
            final @NonNull PlayerViewer viewer,
            final @NonNull InterfaceArguments arguments
    ) {
        return this.open(viewer, arguments, this.title);
    }

    @Override
    public @NonNull ChestView open(
            final @NonNull PlayerViewer viewer,
            final @NonNull Component title
    ) {
        return this.open(viewer, HashMapInterfaceArguments.empty(), title);
    }

    @Override
    public @NonNull ChestView open(
            final @NonNull PlayerViewer viewer,
            final @NonNull InterfaceArguments arguments,
            final @NonNull Component title
    ) {
        final @NonNull ChestView view = new ChestView(this, viewer, arguments, title);

        view.open();

        return view;
    }

    @Override
    public @NonNull ChestView open(
            final @NonNull InterfaceView<?, PlayerViewer> parent,
            final @NonNull InterfaceArguments arguments
    ) {
        final @NonNull ChestView view = new ChestView((PlayerView<?>) parent, this, parent.viewer(), arguments, this.title);

        view.open();

        return view;
    }


    /**
     * Sets the title of the interface.
     *
     * @return the title
     */
    @Override
    public @NonNull Component title() {
        return this.title;
    }

    /**
     * Returns true if updating interface, false if not.
     *
     * @return true if updating interface, false if not
     */
    @Override
    public boolean updates() {
        return this.updates;
    }

    /**
     * Returns the update delay.
     *
     * @return the update delay
     */
    @Override
    public int updateDelay() {
        return this.updateDelay;
    }

    /**
     * A class that builds a chest interface.
     */
    public static final class Builder implements Interface.Builder<ChestPane, PlayerViewer, ChestInterface> {

        /**
         * The list of transformations.
         */
        private final @NonNull List<@NonNull TransformContext<?, ChestPane, PlayerViewer>> transformsList;

        /**
         * The list of close handlers.
         */
        private final @NonNull List<@NonNull CloseHandler<ChestPane>> closeHandlerList;

        /**
         * The amount of rows.
         */
        private final int rows;

        /**
         * The title.
         */
        private final @NonNull Component title;

        /**
         * True if updating interface, false if not.
         */
        private final boolean updates;

        /**
         * How many ticks to wait between interface updates.
         */
        private final int updateDelay;

        /**
         * The top click handler.
         */
        private final @NonNull ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer, InventoryClickContext<ChestPane,
                ChestView>> clickHandler;

        /**
         * Constructs {@code Builder}.
         */
        public Builder() {
            this.transformsList = new ArrayList<>();
            this.closeHandlerList = new ArrayList<>();
            this.rows = 1;
            this.title = Component.empty();
            this.updates = false;
            this.updateDelay = 1;
            this.clickHandler = ClickHandler.cancel();
        }

        private Builder(
                final @NonNull List<TransformContext<?, ChestPane, PlayerViewer>> transformsList,
                final @NonNull List<CloseHandler<ChestPane>> closeHandlerList,
                final int rows,
                final @NonNull Component title,
                final boolean updates,
                final int updateDelay,
                final @NonNull ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer, InventoryClickContext<ChestPane,
                        ChestView>> clickHandler
        ) {
            this.transformsList = Collections.unmodifiableList(transformsList);
            this.closeHandlerList = Collections.unmodifiableList(closeHandlerList);
            this.rows = rows;
            this.title = title;
            this.updates = updates;
            this.updateDelay = updateDelay;
            this.clickHandler = clickHandler;
        }

        /**
         * Returns the number of rows for the interface.
         *
         * @return the number of rows
         */
        public int rows() {
            return this.rows;
        }

        /**
         * Sets the number of rows for the interface.
         *
         * @param rows the number of rows
         * @return new builder instance
         */
        public @NonNull Builder rows(final int rows) {
            return new Builder(
                    this.transformsList,
                    this.closeHandlerList,
                    rows,
                    this.title,
                    this.updates,
                    this.updateDelay,
                    this.clickHandler
            );
        }

        /**
         * Returns the title of the interface.
         *
         * @return the title
         */
        public @NonNull Component title() {
            return this.title;
        }

        /**
         * Sets the title of the interface.
         *
         * @param title the title
         * @return new builder instance
         */
        public @NonNull Builder title(final @NonNull Component title) {
            return new Builder(
                    this.transformsList,
                    this.closeHandlerList,
                    this.rows,
                    title,
                    this.updates,
                    this.updateDelay,
                    this.clickHandler
            );
        }

        /**
         * Adds a close handler to the interface.
         *
         * @param closeHandler the close handler
         * @return new builder instance.
         */
        public @NonNull Builder addCloseHandler(final @NonNull CloseHandler<ChestPane> closeHandler) {
            final List<CloseHandler<ChestPane>> closeHandlers = new ArrayList<>(this.closeHandlerList);
            closeHandlers.add(closeHandler);

            return new Builder(
                    this.transformsList,
                    closeHandlers,
                    this.rows,
                    this.title,
                    this.updates,
                    this.updateDelay,
                    this.clickHandler
            );
        }

        /**
         * Adds a transformation to the interface.
         *
         * @param transform the transformation
         * @return new builder instance.
         */
        @Override
        public @NonNull <T> Builder addTransform(
                final @NonNull InterfaceProperty<T> property,
                final int priority,
                final @NonNull Transform<ChestPane, PlayerViewer> transform
        ) {
            final List<TransformContext<?, ChestPane, PlayerViewer>> transforms = new ArrayList<>(this.transformsList);
            transforms.add(
                    TransformContext.of(
                            property,
                            priority,
                            transform
                    )
            );

            return new Builder(
                    transforms,
                    this.closeHandlerList,
                    this.rows,
                    this.title,
                    this.updates,
                    this.updateDelay,
                    this.clickHandler
            );
        }

        /**
         * Adds a transformation to the interface.
         *
         * @param transform the transformation
         * @return new builder instance.
         */
        @Override
        public @NonNull Builder addTransform(final @NonNull Transform<ChestPane, PlayerViewer> transform) {
            return this.addTransform(InterfaceProperty.dummy(), 1, transform);
        }

        /**
         * Returns the click handler.
         *
         * @return click handler
         */
        public @NonNull ClickHandler<ChestPane, InventoryClickEvent, PlayerViewer,
                InventoryClickContext<ChestPane, ChestView>> clickHandler() {
            return this.clickHandler;
        }

        /**
         * Sets the click handler.
         *
         * @param handler the handler
         * @return new builder instance
         */
        public @NonNull Builder clickHandler(final @NonNull ClickHandler<ChestPane, InventoryClickEvent,
                PlayerViewer, InventoryClickContext<ChestPane, ChestView>> handler) {
            return new Builder(
                    this.transformsList,
                    this.closeHandlerList,
                    this.rows,
                    this.title,
                    this.updates,
                    this.updateDelay,
                    handler
            );
        }

        /**
         * Controls how/if the interface updates.
         *
         * @param updates     true if the interface should update, false if not
         * @param updateDelay how many ticks to wait between updates
         * @return new builder instance
         */
        public @NonNull Builder updates(final boolean updates, final int updateDelay) {
            return new Builder(
                    this.transformsList,
                    this.closeHandlerList,
                    this.rows,
                    this.title,
                    updates,
                    updateDelay,
                    this.clickHandler
            );
        }

        /**
         * Constructs and returns the interface.
         *
         * @return the interface
         */
        @Override
        public @NonNull ChestInterface build() {
            return new ChestInterface(
                    this.rows,
                    this.title,
                    this.transformsList,
                    this.closeHandlerList,
                    this.updates,
                    this.updateDelay,
                    this.clickHandler
            );
        }

    }

}

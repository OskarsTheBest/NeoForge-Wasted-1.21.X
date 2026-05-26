package site.otools.Wasted.block.entity;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;


public class RecyclerItemHandler implements IItemHandlerModifiable {
    private final IItemHandlerModifiable delegate;
    private final int firstInput;
    private final int lastInput;
    private final int firstOutput;
    private final int lastOutput;

    public RecyclerItemHandler(IItemHandlerModifiable delegate,
                               int firstInput, int lastInput,
                               int firstOutput, int lastOutput) {
        this.delegate = delegate;
        this.firstInput = firstInput;
        this.lastInput = lastInput;
        this.firstOutput = firstOutput;
        this.lastOutput = lastOutput;
    }

    private boolean isInput(int slot) {
        return slot >= firstInput && slot <= lastInput;
    }

    private boolean isOutput(int slot) {
        return slot >= firstOutput && slot <= lastOutput;
    }

    @Override
    public int getSlots() {
        return delegate.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return delegate.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (!isInput(slot)) return stack;
        return delegate.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!isOutput(slot)) return ItemStack.EMPTY;
        return delegate.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return delegate.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return isInput(slot) && delegate.isItemValid(slot, stack);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        delegate.setStackInSlot(slot, stack);
    }
}

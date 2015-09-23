package io.github.xxyy.mtc.module.shop;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

class MyPlayerInventory implements PlayerInventory {

    @NotNull
    private final Player plr;

    private ItemStack[] armor = new ItemStack[4];
    private ItemStack[] inv = new ItemStack[4 * 9];
    private int heldItemSlot = 0;

    MyPlayerInventory(@NotNull Player plr) {
        this.plr = plr;
    }

    @Override
    public ItemStack[] getArmorContents() {
        return armor;
    }

    @Override
    public ItemStack getHelmet() {
        return armor[0];
    }

    @Override
    public ItemStack getChestplate() {
        return armor[1];
    }

    @Override
    public ItemStack getLeggings() {
        return armor[2];
    }

    @Override
    public ItemStack getBoots() {
        return armor[3];
    }

    @Override
    public void setArmorContents(ItemStack[] itemStacks) {
        armor = itemStacks;
    }

    @Override
    public void setHelmet(ItemStack itemStack) {
        armor[0] = itemStack;
    }

    @Override
    public void setChestplate(ItemStack itemStack) {
        armor[1] = itemStack;
    }

    @Override
    public void setLeggings(ItemStack itemStack) {
        armor[2] = itemStack;
    }

    @Override
    public void setBoots(ItemStack itemStack) {
        armor[3] = itemStack;
    }

    @Override
    public ItemStack getItemInHand() {
        return null;
    }

    @Override
    public void setItemInHand(ItemStack itemStack) {
        inv[heldItemSlot] = itemStack;
    }

    @Override
    public int getHeldItemSlot() {
        return heldItemSlot;
    }

    @Override
    public void setHeldItemSlot(int i) {
        heldItemSlot = i;
    }

    @Override
    public int clear(int i, int i1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HumanEntity getHolder() {
        return plr;
    }

    @Override
    public int getSize() {
        return inv.length;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setMaxStackSize(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ItemStack getItem(int i) {
        return inv[i];
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        inv[i] = itemStack;
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... itemStacks) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... itemStacks) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack[] getContents() {
        return Arrays.copyOf(inv, inv.length);
    }

    @Override
    public void setContents(ItemStack[] itemStacks) throws IllegalArgumentException {
        inv = itemStacks;
    }

    @Override
    public boolean contains(int i) {
        return Arrays.stream(inv)
                .filter(itemStack -> itemStack.getType().getId() == i)
                .count() > 0;
    }

    @Override
    public boolean contains(Material material) throws IllegalArgumentException {
        return Arrays.stream(inv)
                .filter(Objects::nonNull)
                .filter(itemStack -> itemStack.getType() == material)
                .count() > 0;
    }

    @Override
    public boolean contains(ItemStack is) {
        return Arrays.stream(inv)
                .filter(Objects::nonNull)
                .filter(is::equals)
                .count() > 0;
    }

    @Override
    public boolean contains(int i, int i1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Material material, int i) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(ItemStack itemStack, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAtLeast(ItemStack itemStack, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int first(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int first(Material material) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int first(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int firstEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(Material material) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        inv = new ItemStack[getSize()];
    }

    @Override
    public List<HumanEntity> getViewers() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTitle() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InventoryType getType() {
        return InventoryType.PLAYER;
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<ItemStack> iterator(int i) {
        throw new UnsupportedOperationException();
    }
}

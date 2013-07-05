/*     */ package it.unimi.dsi.fastutil.floats;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.Hash;
/*     */ import it.unimi.dsi.fastutil.HashCommon;
/*     */ import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
/*     */ import it.unimi.dsi.fastutil.booleans.BooleanArrays;
/*     */ import it.unimi.dsi.fastutil.booleans.BooleanCollection;
/*     */ import it.unimi.dsi.fastutil.booleans.BooleanIterator;
/*     */ import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectIterator;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ public class Float2BooleanOpenCustomHashMap extends AbstractFloat2BooleanMap
/*     */   implements Serializable, Cloneable, Hash
/*     */ {
/*     */   public static final long serialVersionUID = 0L;
/*     */   private static final boolean ASSERTS = false;
/*     */   protected transient float[] key;
/*     */   protected transient boolean[] value;
/*     */   protected transient boolean[] used;
/*     */   protected final float f;
/*     */   protected transient int n;
/*     */   protected transient int maxFill;
/*     */   protected transient int mask;
/*     */   protected int size;
/*     */   protected volatile transient Float2BooleanMap.FastEntrySet entries;
/*     */   protected volatile transient FloatSet keys;
/*     */   protected volatile transient BooleanCollection values;
/*     */   protected FloatHash.Strategy strategy;
/*     */ 
/*     */   public Float2BooleanOpenCustomHashMap(int expected, float f, FloatHash.Strategy strategy)
/*     */   {
/* 111 */     this.strategy = strategy;
/* 112 */     if ((f <= 0.0F) || (f > 1.0F)) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
/* 113 */     if (expected < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
/* 114 */     this.f = f;
/* 115 */     this.n = HashCommon.arraySize(expected, f);
/* 116 */     this.mask = (this.n - 1);
/* 117 */     this.maxFill = HashCommon.maxFill(this.n, f);
/* 118 */     this.key = new float[this.n];
/* 119 */     this.value = new boolean[this.n];
/* 120 */     this.used = new boolean[this.n];
/*     */   }
/*     */ 
/*     */   public Float2BooleanOpenCustomHashMap(int expected, FloatHash.Strategy strategy)
/*     */   {
/* 128 */     this(expected, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public Float2BooleanOpenCustomHashMap(FloatHash.Strategy strategy)
/*     */   {
/* 135 */     this(16, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public Float2BooleanOpenCustomHashMap(Map<? extends Float, ? extends Boolean> m, float f, FloatHash.Strategy strategy)
/*     */   {
/* 144 */     this(m.size(), f, strategy);
/* 145 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Float2BooleanOpenCustomHashMap(Map<? extends Float, ? extends Boolean> m, FloatHash.Strategy strategy)
/*     */   {
/* 153 */     this(m, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public Float2BooleanOpenCustomHashMap(Float2BooleanMap m, float f, FloatHash.Strategy strategy)
/*     */   {
/* 162 */     this(m.size(), f, strategy);
/* 163 */     putAll(m);
/*     */   }
/*     */ 
/*     */   public Float2BooleanOpenCustomHashMap(Float2BooleanMap m, FloatHash.Strategy strategy)
/*     */   {
/* 171 */     this(m, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public Float2BooleanOpenCustomHashMap(float[] k, boolean[] v, float f, FloatHash.Strategy strategy)
/*     */   {
/* 182 */     this(k.length, f, strategy);
/* 183 */     if (k.length != v.length) throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
/* 184 */     for (int i = 0; i < k.length; i++) put(k[i], v[i]);
/*     */   }
/*     */ 
/*     */   public Float2BooleanOpenCustomHashMap(float[] k, boolean[] v, FloatHash.Strategy strategy)
/*     */   {
/* 194 */     this(k, v, 0.75F, strategy);
/*     */   }
/*     */ 
/*     */   public FloatHash.Strategy strategy()
/*     */   {
/* 201 */     return this.strategy;
/*     */   }
/*     */ 
/*     */   public boolean put(float k, boolean v)
/*     */   {
/* 209 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 211 */     while (this.used[pos] != 0) {
/* 212 */       if (this.strategy.equals(this.key[pos], k)) {
/* 213 */         boolean oldValue = this.value[pos];
/* 214 */         this.value[pos] = v;
/* 215 */         return oldValue;
/*     */       }
/* 217 */       pos = pos + 1 & this.mask;
/*     */     }
/* 219 */     this.used[pos] = true;
/* 220 */     this.key[pos] = k;
/* 221 */     this.value[pos] = v;
/* 222 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*     */ 
/* 224 */     return this.defRetValue;
/*     */   }
/*     */   public Boolean put(Float ok, Boolean ov) {
/* 227 */     boolean v = ov.booleanValue();
/* 228 */     float k = ok.floatValue();
/*     */ 
/* 230 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 232 */     while (this.used[pos] != 0) {
/* 233 */       if (this.strategy.equals(this.key[pos], k)) {
/* 234 */         Boolean oldValue = Boolean.valueOf(this.value[pos]);
/* 235 */         this.value[pos] = v;
/* 236 */         return oldValue;
/*     */       }
/* 238 */       pos = pos + 1 & this.mask;
/*     */     }
/* 240 */     this.used[pos] = true;
/* 241 */     this.key[pos] = k;
/* 242 */     this.value[pos] = v;
/* 243 */     if (++this.size >= this.maxFill) rehash(HashCommon.arraySize(this.size + 1, this.f));
/*     */ 
/* 245 */     return null;
/*     */   }
/*     */ 
/*     */   protected final int shiftKeys(int pos)
/*     */   {
/*     */     int last;
/*     */     while (true)
/*     */     {
/* 257 */       pos = (last = pos) + 1 & this.mask;
/* 258 */       while (this.used[pos] != 0) {
/* 259 */         int slot = HashCommon.murmurHash3(this.strategy.hashCode(this.key[pos])) & this.mask;
/* 260 */         if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/* 261 */         pos = pos + 1 & this.mask;
/*     */       }
/* 263 */       if (this.used[pos] == 0) break;
/* 264 */       this.key[last] = this.key[pos];
/* 265 */       this.value[last] = this.value[pos];
/*     */     }
/* 267 */     this.used[last] = false;
/* 268 */     return last;
/*     */   }
/*     */ 
/*     */   public boolean remove(float k)
/*     */   {
/* 273 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 275 */     while (this.used[pos] != 0) {
/* 276 */       if (this.strategy.equals(this.key[pos], k)) {
/* 277 */         this.size -= 1;
/* 278 */         boolean v = this.value[pos];
/* 279 */         shiftKeys(pos);
/* 280 */         return v;
/*     */       }
/* 282 */       pos = pos + 1 & this.mask;
/*     */     }
/* 284 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public Boolean remove(Object ok) {
/* 288 */     float k = ((Float)ok).floatValue();
/*     */ 
/* 290 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 292 */     while (this.used[pos] != 0) {
/* 293 */       if (this.strategy.equals(this.key[pos], k)) {
/* 294 */         this.size -= 1;
/* 295 */         boolean v = this.value[pos];
/* 296 */         shiftKeys(pos);
/* 297 */         return Boolean.valueOf(v);
/*     */       }
/* 299 */       pos = pos + 1 & this.mask;
/*     */     }
/* 301 */     return null;
/*     */   }
/*     */   public Boolean get(Float ok) {
/* 304 */     float k = ok.floatValue();
/*     */ 
/* 306 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 308 */     while (this.used[pos] != 0) {
/* 309 */       if (this.strategy.equals(this.key[pos], k)) return Boolean.valueOf(this.value[pos]);
/* 310 */       pos = pos + 1 & this.mask;
/*     */     }
/* 312 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean get(float k)
/*     */   {
/* 317 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 319 */     while (this.used[pos] != 0) {
/* 320 */       if (this.strategy.equals(this.key[pos], k)) return this.value[pos];
/* 321 */       pos = pos + 1 & this.mask;
/*     */     }
/* 323 */     return this.defRetValue;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(float k)
/*     */   {
/* 328 */     int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/*     */ 
/* 330 */     while (this.used[pos] != 0) {
/* 331 */       if (this.strategy.equals(this.key[pos], k)) return true;
/* 332 */       pos = pos + 1 & this.mask;
/*     */     }
/* 334 */     return false;
/*     */   }
/*     */   public boolean containsValue(boolean v) {
/* 337 */     boolean[] value = this.value;
/* 338 */     boolean[] used = this.used;
/* 339 */     for (int i = this.n; i-- != 0; return true) label16: if ((used[i] == 0) || (value[i] != v))
/*     */         break label16; return false;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 349 */     if (this.size == 0) return;
/* 350 */     this.size = 0;
/* 351 */     BooleanArrays.fill(this.used, false);
/*     */   }
/*     */ 
/*     */   public int size() {
/* 355 */     return this.size;
/*     */   }
/*     */   public boolean isEmpty() {
/* 358 */     return this.size == 0;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void growthFactor(int growthFactor)
/*     */   {
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public int growthFactor()
/*     */   {
/* 375 */     return 16;
/*     */   }
/*     */ 
/*     */   public Float2BooleanMap.FastEntrySet float2BooleanEntrySet()
/*     */   {
/* 581 */     if (this.entries == null) this.entries = new MapEntrySet(null);
/* 582 */     return this.entries;
/*     */   }
/*     */ 
/*     */   public FloatSet keySet()
/*     */   {
/* 615 */     if (this.keys == null) this.keys = new KeySet(null);
/* 616 */     return this.keys;
/*     */   }
/*     */ 
/*     */   public BooleanCollection values()
/*     */   {
/* 630 */     if (this.values == null) this.values = new AbstractBooleanCollection() {
/*     */         public BooleanIterator iterator() {
/* 632 */           return new Float2BooleanOpenCustomHashMap.ValueIterator(Float2BooleanOpenCustomHashMap.this);
/*     */         }
/*     */         public int size() {
/* 635 */           return Float2BooleanOpenCustomHashMap.this.size;
/*     */         }
/*     */         public boolean contains(boolean v) {
/* 638 */           return Float2BooleanOpenCustomHashMap.this.containsValue(v);
/*     */         }
/*     */         public void clear() {
/* 641 */           Float2BooleanOpenCustomHashMap.this.clear();
/*     */         }
/*     */       };
/* 644 */     return this.values;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean rehash()
/*     */   {
/* 658 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean trim()
/*     */   {
/* 673 */     int l = HashCommon.arraySize(this.size, this.f);
/* 674 */     if (l >= this.n) return true; try
/*     */     {
/* 676 */       rehash(l);
/*     */     } catch (OutOfMemoryError cantDoIt) {
/* 678 */       return false;
/* 679 */     }return true;
/*     */   }
/*     */ 
/*     */   public boolean trim(int n)
/*     */   {
/* 700 */     int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / this.f));
/* 701 */     if (this.n <= l) return true; try
/*     */     {
/* 703 */       rehash(l);
/*     */     } catch (OutOfMemoryError cantDoIt) {
/* 705 */       return false;
/* 706 */     }return true;
/*     */   }
/*     */ 
/*     */   protected void rehash(int newN)
/*     */   {
/* 719 */     int i = 0;
/* 720 */     boolean[] used = this.used;
/*     */ 
/* 722 */     float[] key = this.key;
/* 723 */     boolean[] value = this.value;
/* 724 */     int newMask = newN - 1;
/* 725 */     float[] newKey = new float[newN];
/* 726 */     boolean[] newValue = new boolean[newN];
/* 727 */     boolean[] newUsed = new boolean[newN];
/* 728 */     for (int j = this.size; j-- != 0; ) {
/* 729 */       while (used[i] == 0) i++;
/* 730 */       float k = key[i];
/* 731 */       int pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & newMask;
/* 732 */       while (newUsed[pos] != 0) pos = pos + 1 & newMask;
/* 733 */       newUsed[pos] = true;
/* 734 */       newKey[pos] = k;
/* 735 */       newValue[pos] = value[i];
/* 736 */       i++;
/*     */     }
/* 738 */     this.n = newN;
/* 739 */     this.mask = newMask;
/* 740 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 741 */     this.key = newKey;
/* 742 */     this.value = newValue;
/* 743 */     this.used = newUsed;
/*     */   }
/*     */ 
/*     */   public Float2BooleanOpenCustomHashMap clone()
/*     */   {
/*     */     Float2BooleanOpenCustomHashMap c;
/*     */     try
/*     */     {
/* 756 */       c = (Float2BooleanOpenCustomHashMap)super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException cantHappen) {
/* 759 */       throw new InternalError();
/*     */     }
/* 761 */     c.keys = null;
/* 762 */     c.values = null;
/* 763 */     c.entries = null;
/* 764 */     c.key = ((float[])this.key.clone());
/* 765 */     c.value = ((boolean[])this.value.clone());
/* 766 */     c.used = ((boolean[])this.used.clone());
/* 767 */     c.strategy = this.strategy;
/* 768 */     return c;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 780 */     int h = 0;
/* 781 */     int j = this.size; int i = 0; for (int t = 0; j-- != 0; ) {
/* 782 */       while (this.used[i] == 0) i++;
/* 783 */       t = this.strategy.hashCode(this.key[i]);
/* 784 */       t ^= (this.value[i] != 0 ? 1231 : 1237);
/* 785 */       h += t;
/* 786 */       i++;
/*     */     }
/* 788 */     return h;
/*     */   }
/*     */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 791 */     float[] key = this.key;
/* 792 */     boolean[] value = this.value;
/* 793 */     MapIterator i = new MapIterator(null);
/* 794 */     s.defaultWriteObject();
/* 795 */     for (int j = this.size; j-- != 0; ) {
/* 796 */       int e = i.nextEntry();
/* 797 */       s.writeFloat(key[e]);
/* 798 */       s.writeBoolean(value[e]);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 803 */     s.defaultReadObject();
/* 804 */     this.n = HashCommon.arraySize(this.size, this.f);
/* 805 */     this.maxFill = HashCommon.maxFill(this.n, this.f);
/* 806 */     this.mask = (this.n - 1);
/* 807 */     float[] key = this.key = new float[this.n];
/* 808 */     boolean[] value = this.value = new boolean[this.n];
/* 809 */     boolean[] used = this.used = new boolean[this.n];
/*     */ 
/* 812 */     int i = this.size; for (int pos = 0; i-- != 0; ) {
/* 813 */       float k = s.readFloat();
/* 814 */       boolean v = s.readBoolean();
/* 815 */       pos = HashCommon.murmurHash3(this.strategy.hashCode(k)) & this.mask;
/* 816 */       while (used[pos] != 0) pos = pos + 1 & this.mask;
/* 817 */       used[pos] = true;
/* 818 */       key[pos] = k;
/* 819 */       value[pos] = v;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkTable()
/*     */   {
/*     */   }
/*     */ 
/*     */   private final class ValueIterator extends Float2BooleanOpenCustomHashMap.MapIterator
/*     */     implements BooleanIterator
/*     */   {
/*     */     public ValueIterator()
/*     */     {
/* 625 */       super(null); } 
/* 626 */     public boolean nextBoolean() { return Float2BooleanOpenCustomHashMap.this.value[nextEntry()]; } 
/* 627 */     public Boolean next() { return Boolean.valueOf(Float2BooleanOpenCustomHashMap.this.value[nextEntry()]); }
/*     */ 
/*     */   }
/*     */ 
/*     */   private final class KeySet extends AbstractFloatSet
/*     */   {
/*     */     private KeySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public FloatIterator iterator()
/*     */     {
/* 597 */       return new Float2BooleanOpenCustomHashMap.KeyIterator(Float2BooleanOpenCustomHashMap.this);
/*     */     }
/*     */     public int size() {
/* 600 */       return Float2BooleanOpenCustomHashMap.this.size;
/*     */     }
/*     */     public boolean contains(float k) {
/* 603 */       return Float2BooleanOpenCustomHashMap.this.containsKey(k);
/*     */     }
/*     */     public boolean remove(float k) {
/* 606 */       int oldSize = Float2BooleanOpenCustomHashMap.this.size;
/* 607 */       Float2BooleanOpenCustomHashMap.this.remove(k);
/* 608 */       return Float2BooleanOpenCustomHashMap.this.size != oldSize;
/*     */     }
/*     */     public void clear() {
/* 611 */       Float2BooleanOpenCustomHashMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class KeyIterator extends Float2BooleanOpenCustomHashMap.MapIterator
/*     */     implements FloatIterator
/*     */   {
/*     */     public KeyIterator()
/*     */     {
/* 591 */       super(null); } 
/* 592 */     public float nextFloat() { return Float2BooleanOpenCustomHashMap.this.key[nextEntry()]; } 
/* 593 */     public Float next() { return Float.valueOf(Float2BooleanOpenCustomHashMap.this.key[nextEntry()]); }
/*     */ 
/*     */   }
/*     */ 
/*     */   private final class MapEntrySet extends AbstractObjectSet<Float2BooleanMap.Entry>
/*     */     implements Float2BooleanMap.FastEntrySet
/*     */   {
/*     */     private MapEntrySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public ObjectIterator<Float2BooleanMap.Entry> iterator()
/*     */     {
/* 537 */       return new Float2BooleanOpenCustomHashMap.EntryIterator(Float2BooleanOpenCustomHashMap.this, null);
/*     */     }
/*     */     public ObjectIterator<Float2BooleanMap.Entry> fastIterator() {
/* 540 */       return new Float2BooleanOpenCustomHashMap.FastEntryIterator(Float2BooleanOpenCustomHashMap.this, null);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o) {
/* 544 */       if (!(o instanceof Map.Entry)) return false;
/* 545 */       Map.Entry e = (Map.Entry)o;
/* 546 */       float k = ((Float)e.getKey()).floatValue();
/*     */ 
/* 548 */       int pos = HashCommon.murmurHash3(Float2BooleanOpenCustomHashMap.this.strategy.hashCode(k)) & Float2BooleanOpenCustomHashMap.this.mask;
/*     */ 
/* 550 */       while (Float2BooleanOpenCustomHashMap.this.used[pos] != 0) {
/* 551 */         if (Float2BooleanOpenCustomHashMap.this.strategy.equals(Float2BooleanOpenCustomHashMap.this.key[pos], k)) return Float2BooleanOpenCustomHashMap.this.value[pos] == ((Boolean)e.getValue()).booleanValue();
/* 552 */         pos = pos + 1 & Float2BooleanOpenCustomHashMap.this.mask;
/*     */       }
/* 554 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean remove(Object o) {
/* 558 */       if (!(o instanceof Map.Entry)) return false;
/* 559 */       Map.Entry e = (Map.Entry)o;
/* 560 */       float k = ((Float)e.getKey()).floatValue();
/*     */ 
/* 562 */       int pos = HashCommon.murmurHash3(Float2BooleanOpenCustomHashMap.this.strategy.hashCode(k)) & Float2BooleanOpenCustomHashMap.this.mask;
/*     */ 
/* 564 */       while (Float2BooleanOpenCustomHashMap.this.used[pos] != 0) {
/* 565 */         if (Float2BooleanOpenCustomHashMap.this.strategy.equals(Float2BooleanOpenCustomHashMap.this.key[pos], k)) {
/* 566 */           Float2BooleanOpenCustomHashMap.this.remove(e.getKey());
/* 567 */           return true;
/*     */         }
/* 569 */         pos = pos + 1 & Float2BooleanOpenCustomHashMap.this.mask;
/*     */       }
/* 571 */       return false;
/*     */     }
/*     */     public int size() {
/* 574 */       return Float2BooleanOpenCustomHashMap.this.size;
/*     */     }
/*     */     public void clear() {
/* 577 */       Float2BooleanOpenCustomHashMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class FastEntryIterator extends Float2BooleanOpenCustomHashMap.MapIterator
/*     */     implements ObjectIterator<Float2BooleanMap.Entry>
/*     */   {
/* 527 */     final AbstractFloat2BooleanMap.BasicEntry entry = new AbstractFloat2BooleanMap.BasicEntry(0.0F, false);
/*     */ 
/*     */     private FastEntryIterator()
/*     */     {
/* 526 */       super(null);
/*     */     }
/*     */     public AbstractFloat2BooleanMap.BasicEntry next() {
/* 529 */       int e = nextEntry();
/* 530 */       this.entry.key = Float2BooleanOpenCustomHashMap.this.key[e];
/* 531 */       this.entry.value = Float2BooleanOpenCustomHashMap.this.value[e];
/* 532 */       return this.entry;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class EntryIterator extends Float2BooleanOpenCustomHashMap.MapIterator
/*     */     implements ObjectIterator<Float2BooleanMap.Entry>
/*     */   {
/*     */     private Float2BooleanOpenCustomHashMap.MapEntry entry;
/*     */ 
/*     */     private EntryIterator()
/*     */     {
/* 515 */       super(null);
/*     */     }
/*     */     public Float2BooleanMap.Entry next() {
/* 518 */       return this.entry = new Float2BooleanOpenCustomHashMap.MapEntry(Float2BooleanOpenCustomHashMap.this, nextEntry());
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 522 */       super.remove();
/* 523 */       Float2BooleanOpenCustomHashMap.MapEntry.access$102(this.entry, -1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class MapIterator
/*     */   {
/*     */     int pos;
/*     */     int last;
/*     */     int c;
/*     */     FloatArrayList wrapped;
/*     */ 
/*     */     private MapIterator()
/*     */     {
/* 424 */       this.pos = Float2BooleanOpenCustomHashMap.this.n;
/*     */ 
/* 427 */       this.last = -1;
/*     */ 
/* 429 */       this.c = Float2BooleanOpenCustomHashMap.this.size;
/*     */ 
/* 434 */       boolean[] used = Float2BooleanOpenCustomHashMap.this.used;
/* 435 */       while ((this.c != 0) && (used[(--this.pos)] == 0));
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 438 */       return this.c != 0;
/*     */     }
/*     */     public int nextEntry() {
/* 441 */       if (!hasNext()) throw new NoSuchElementException();
/* 442 */       this.c -= 1;
/*     */ 
/* 444 */       if (this.pos < 0) {
/* 445 */         float k = this.wrapped.getFloat(-(this.last = --this.pos) - 2);
/*     */ 
/* 447 */         int pos = HashCommon.murmurHash3(Float2BooleanOpenCustomHashMap.this.strategy.hashCode(k)) & Float2BooleanOpenCustomHashMap.this.mask;
/*     */ 
/* 449 */         while (Float2BooleanOpenCustomHashMap.this.used[pos] != 0) {
/* 450 */           if (Float2BooleanOpenCustomHashMap.this.strategy.equals(Float2BooleanOpenCustomHashMap.this.key[pos], k)) return pos;
/* 451 */           pos = pos + 1 & Float2BooleanOpenCustomHashMap.this.mask;
/*     */         }
/*     */       }
/* 454 */       this.last = this.pos;
/*     */ 
/* 456 */       if (this.c != 0) {
/* 457 */         boolean[] used = Float2BooleanOpenCustomHashMap.this.used;
/* 458 */         while ((this.pos-- != 0) && (used[this.pos] == 0));
/*     */       }
/* 461 */       return this.last;
/*     */     }
/*     */ 
/*     */     protected final int shiftKeys(int pos)
/*     */     {
/*     */       int last;
/*     */       while (true)
/*     */       {
/* 474 */         pos = (last = pos) + 1 & Float2BooleanOpenCustomHashMap.this.mask;
/* 475 */         while (Float2BooleanOpenCustomHashMap.this.used[pos] != 0) {
/* 476 */           int slot = HashCommon.murmurHash3(Float2BooleanOpenCustomHashMap.this.strategy.hashCode(Float2BooleanOpenCustomHashMap.this.key[pos])) & Float2BooleanOpenCustomHashMap.this.mask;
/* 477 */           if (last <= pos ? (last < slot) && (slot <= pos) : (last >= slot) && (slot > pos)) break;
/* 478 */           pos = pos + 1 & Float2BooleanOpenCustomHashMap.this.mask;
/*     */         }
/* 480 */         if (Float2BooleanOpenCustomHashMap.this.used[pos] == 0) break;
/* 481 */         if (pos < last)
/*     */         {
/* 483 */           if (this.wrapped == null) this.wrapped = new FloatArrayList();
/* 484 */           this.wrapped.add(Float2BooleanOpenCustomHashMap.this.key[pos]);
/*     */         }
/* 486 */         Float2BooleanOpenCustomHashMap.this.key[last] = Float2BooleanOpenCustomHashMap.this.key[pos];
/* 487 */         Float2BooleanOpenCustomHashMap.this.value[last] = Float2BooleanOpenCustomHashMap.this.value[pos];
/*     */       }
/* 489 */       Float2BooleanOpenCustomHashMap.this.used[last] = false;
/* 490 */       return last;
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 494 */       if (this.last == -1) throw new IllegalStateException();
/* 495 */       if (this.pos < -1)
/*     */       {
/* 497 */         Float2BooleanOpenCustomHashMap.this.remove(this.wrapped.getFloat(-this.pos - 2));
/* 498 */         this.last = -1;
/* 499 */         return;
/*     */       }
/* 501 */       Float2BooleanOpenCustomHashMap.this.size -= 1;
/* 502 */       if ((shiftKeys(this.last) == this.pos) && (this.c > 0)) {
/* 503 */         this.c += 1;
/* 504 */         nextEntry();
/*     */       }
/* 506 */       this.last = -1;
/*     */     }
/*     */ 
/*     */     public int skip(int n) {
/* 510 */       int i = n;
/* 511 */       while ((i-- != 0) && (hasNext())) nextEntry();
/* 512 */       return n - i - 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class MapEntry
/*     */     implements Float2BooleanMap.Entry, Map.Entry<Float, Boolean>
/*     */   {
/*     */     private int index;
/*     */ 
/*     */     MapEntry(int index)
/*     */     {
/* 385 */       this.index = index;
/*     */     }
/*     */     public Float getKey() {
/* 388 */       return Float.valueOf(Float2BooleanOpenCustomHashMap.this.key[this.index]);
/*     */     }
/*     */     public float getFloatKey() {
/* 391 */       return Float2BooleanOpenCustomHashMap.this.key[this.index];
/*     */     }
/*     */     public Boolean getValue() {
/* 394 */       return Boolean.valueOf(Float2BooleanOpenCustomHashMap.this.value[this.index]);
/*     */     }
/*     */     public boolean getBooleanValue() {
/* 397 */       return Float2BooleanOpenCustomHashMap.this.value[this.index];
/*     */     }
/*     */     public boolean setValue(boolean v) {
/* 400 */       boolean oldValue = Float2BooleanOpenCustomHashMap.this.value[this.index];
/* 401 */       Float2BooleanOpenCustomHashMap.this.value[this.index] = v;
/* 402 */       return oldValue;
/*     */     }
/*     */     public Boolean setValue(Boolean v) {
/* 405 */       return Boolean.valueOf(setValue(v.booleanValue()));
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o) {
/* 409 */       if (!(o instanceof Map.Entry)) return false;
/* 410 */       Map.Entry e = (Map.Entry)o;
/* 411 */       return (Float2BooleanOpenCustomHashMap.this.strategy.equals(Float2BooleanOpenCustomHashMap.this.key[this.index], ((Float)e.getKey()).floatValue())) && (Float2BooleanOpenCustomHashMap.this.value[this.index] == ((Boolean)e.getValue()).booleanValue());
/*     */     }
/*     */     public int hashCode() {
/* 414 */       return Float2BooleanOpenCustomHashMap.this.strategy.hashCode(Float2BooleanOpenCustomHashMap.this.key[this.index]) ^ (Float2BooleanOpenCustomHashMap.this.value[this.index] != 0 ? 1231 : 1237);
/*     */     }
/*     */     public String toString() {
/* 417 */       return Float2BooleanOpenCustomHashMap.this.key[this.index] + "=>" + Float2BooleanOpenCustomHashMap.this.value[this.index];
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.floats.Float2BooleanOpenCustomHashMap
 * JD-Core Version:    0.6.2
 */
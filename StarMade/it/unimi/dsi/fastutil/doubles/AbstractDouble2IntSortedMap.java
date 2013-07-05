/*     */ package it.unimi.dsi.fastutil.doubles;
/*     */ 
/*     */ import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
/*     */ import it.unimi.dsi.fastutil.ints.AbstractIntIterator;
/*     */ import it.unimi.dsi.fastutil.ints.IntCollection;
/*     */ import it.unimi.dsi.fastutil.ints.IntIterator;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
/*     */ import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public abstract class AbstractDouble2IntSortedMap extends AbstractDouble2IntMap
/*     */   implements Double2IntSortedMap
/*     */ {
/*     */   public static final long serialVersionUID = -1773560792952436569L;
/*     */ 
/*     */   public Double2IntSortedMap headMap(Double to)
/*     */   {
/*  59 */     return headMap(to.doubleValue());
/*     */   }
/*     */ 
/*     */   public Double2IntSortedMap tailMap(Double from) {
/*  63 */     return tailMap(from.doubleValue());
/*     */   }
/*     */ 
/*     */   public Double2IntSortedMap subMap(Double from, Double to) {
/*  67 */     return subMap(from.doubleValue(), to.doubleValue());
/*     */   }
/*     */ 
/*     */   public Double firstKey() {
/*  71 */     return Double.valueOf(firstDoubleKey());
/*     */   }
/*     */ 
/*     */   public Double lastKey() {
/*  75 */     return Double.valueOf(lastDoubleKey());
/*     */   }
/*     */ 
/*     */   public DoubleSortedSet keySet()
/*     */   {
/*  89 */     return new KeySet();
/*     */   }
/*     */ 
/*     */   public IntCollection values()
/*     */   {
/* 143 */     return new ValuesCollection();
/*     */   }
/*     */ 
/*     */   public ObjectSortedSet<Map.Entry<Double, Integer>> entrySet()
/*     */   {
/* 174 */     return double2IntEntrySet();
/*     */   }
/*     */ 
/*     */   protected static class ValuesIterator extends AbstractIntIterator
/*     */   {
/*     */     protected final ObjectBidirectionalIterator<Map.Entry<Double, Integer>> i;
/*     */ 
/*     */     public ValuesIterator(ObjectBidirectionalIterator<Map.Entry<Double, Integer>> i)
/*     */     {
/* 165 */       this.i = i;
/*     */     }
/*     */     public int nextInt() {
/* 168 */       return ((Integer)((Map.Entry)this.i.next()).getValue()).intValue(); } 
/* 169 */     public boolean hasNext() { return this.i.hasNext(); }
/*     */ 
/*     */   }
/*     */ 
/*     */   protected class ValuesCollection extends AbstractIntCollection
/*     */   {
/*     */     protected ValuesCollection()
/*     */     {
/*     */     }
/*     */ 
/*     */     public IntIterator iterator()
/*     */     {
/* 148 */       return new AbstractDouble2IntSortedMap.ValuesIterator(AbstractDouble2IntSortedMap.this.entrySet().iterator()); } 
/* 149 */     public boolean contains(int k) { return AbstractDouble2IntSortedMap.this.containsValue(k); } 
/* 150 */     public int size() { return AbstractDouble2IntSortedMap.this.size(); } 
/* 151 */     public void clear() { AbstractDouble2IntSortedMap.this.clear(); }
/*     */ 
/*     */   }
/*     */ 
/*     */   protected static class KeySetIterator extends AbstractDoubleBidirectionalIterator
/*     */   {
/*     */     protected final ObjectBidirectionalIterator<Map.Entry<Double, Integer>> i;
/*     */ 
/*     */     public KeySetIterator(ObjectBidirectionalIterator<Map.Entry<Double, Integer>> i)
/*     */     {
/* 118 */       this.i = i;
/*     */     }
/*     */     public double nextDouble() {
/* 121 */       return ((Double)((Map.Entry)this.i.next()).getKey()).doubleValue(); } 
/* 122 */     public double previousDouble() { return ((Double)((Map.Entry)this.i.previous()).getKey()).doubleValue(); } 
/*     */     public boolean hasNext() {
/* 124 */       return this.i.hasNext(); } 
/* 125 */     public boolean hasPrevious() { return this.i.hasPrevious(); }
/*     */ 
/*     */   }
/*     */ 
/*     */   protected class KeySet extends AbstractDoubleSortedSet
/*     */   {
/*     */     protected KeySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean contains(double k)
/*     */     {
/*  93 */       return AbstractDouble2IntSortedMap.this.containsKey(k); } 
/*  94 */     public int size() { return AbstractDouble2IntSortedMap.this.size(); } 
/*  95 */     public void clear() { AbstractDouble2IntSortedMap.this.clear(); } 
/*  96 */     public DoubleComparator comparator() { return AbstractDouble2IntSortedMap.this.comparator(); } 
/*  97 */     public double firstDouble() { return AbstractDouble2IntSortedMap.this.firstDoubleKey(); } 
/*  98 */     public double lastDouble() { return AbstractDouble2IntSortedMap.this.lastDoubleKey(); } 
/*  99 */     public DoubleSortedSet headSet(double to) { return AbstractDouble2IntSortedMap.this.headMap(to).keySet(); } 
/* 100 */     public DoubleSortedSet tailSet(double from) { return AbstractDouble2IntSortedMap.this.tailMap(from).keySet(); } 
/* 101 */     public DoubleSortedSet subSet(double from, double to) { return AbstractDouble2IntSortedMap.this.subMap(from, to).keySet(); } 
/*     */     public DoubleBidirectionalIterator iterator(double from) {
/* 103 */       return new AbstractDouble2IntSortedMap.KeySetIterator(AbstractDouble2IntSortedMap.this.entrySet().iterator(new AbstractDouble2IntMap.BasicEntry(from, 0))); } 
/* 104 */     public DoubleBidirectionalIterator iterator() { return new AbstractDouble2IntSortedMap.KeySetIterator(AbstractDouble2IntSortedMap.this.entrySet().iterator()); }
/*     */ 
/*     */   }
/*     */ }

/* Location:           C:\Users\Raul\Desktop\StarMade\StarMade.jar
 * Qualified Name:     it.unimi.dsi.fastutil.doubles.AbstractDouble2IntSortedMap
 * JD-Core Version:    0.6.2
 */
package de.bsvrz.iav.gllib.gllib.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import java.util.SortedSet;

/**
 * Eine sortierte Liste.
 * <p>
 * TODO: Generic auf "extends Comparable" hinbiegen.
 * 
 * @author BitCtrl Systems GmbH, Schumann
 * @version $Id$
 * @param <E>
 */
public class SortierteListe<E extends Comparable<? super E>> extends
		AbstractList<E> implements List<E>, SortedSet<E>, Cloneable,
		RandomAccess, Serializable {

	/** ID zur Serialisierung. */
	private static final long serialVersionUID = 1L;

	/** Interner Buffer der Listenelemente. */
	private transient E[] elementData;

	/** Aktuelle Gr&ouml;&szlig;e der Liste, <em>nicht</em> des Buffers. */
	private int size;

	/**
	 * Sortiert eine Liste mit Hilfe von Mergesort. Im Gegensatz zur
	 * Sun-Implementation in {@link java.util.Arrays} werden bereits sortierte
	 * Teillisten ber&uuml;cksichtigt.
	 * 
	 * @param <T>
	 *            Typ der zu sortiernden Elemente
	 * @param list
	 *            Die zu sortierende Liste
	 * @return Eine sortierte Kopie der Ausgangsliste
	 */
	public static <T extends Comparable<? super T>> List<T> mergesort(
			List<T> list) {
		List<T> aux;
		List<Integer> runs;

		// Eine Liste mit einem Element ist a priori sortiert
		if (list.size() == 1) {
			return new ArrayList<T>(list);
		}

		// Sortierte Teilfolgen bestimmen
		runs = new ArrayList<Integer>();
		runs.add(0);
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i - 1).compareTo(list.get(i)) > 0) {
				runs.add(i);
			}
		}
		runs.add(list.size());

		// Die Teilfolgen mergen
		aux = new ArrayList<T>();
		for (int i = 0; i < runs.size() - 1; i++) {
			aux = merge(aux, list, runs.get(i), runs.get(i + 1));
		}

		return aux;
	}

	/**
	 * F&uuml;hrt den merge-Schritt des Mergesort aus.
	 * 
	 * @param <T>
	 *            Typ der zu sortierenden Elemente
	 * @param sorted
	 *            Bereits sortierte Teilliste
	 * @param list
	 *            Die komplette zu sortierende Liste
	 * @param start
	 *            Startindex des zu sortierenden Bereichs der Liste
	 * @param end
	 *            Endindex (exklusive) des zu sortierenden Bereichs der Liste
	 * @return Die sortierte Teilleiste bestehend aus den Elementen aus
	 *         {@code sorted} und {@code list}
	 */
	private static <T extends Comparable<? super T>> List<T> merge(
			List<T> sorted, List<T> list, int start, int end) {
		List<T> merge;
		int j, k;

		merge = new ArrayList<T>();
		j = start;
		k = 0;
		while (k < sorted.size() && j < end) {
			if (sorted.get(k).compareTo(list.get(j)) <= 0) {
				merge.add(sorted.get(k++));
			} else {
				merge.add(list.get(j++));
			}
		}
		for (int i = k; i < sorted.size(); i++) {
			merge.add(sorted.get(i));
		}
		for (int i = j; i < end; i++) {
			merge.add(list.get(i));
		}

		return merge;
	}

	/**
	 * Wendet den Mergesort-Algorithmus an, um die Listenelemente zu sortieren.
	 */
	private void mergesort() {
		List<E> sortiert;

		sortiert = new ArrayList<E>(Arrays.asList(elementData));
		while (sortiert.remove(null)) {
			// nix weiter
		}
		sortiert = mergesort(sortiert);

		assert elementData.length == sortiert.size();
		for (int i = 0; i < sortiert.size(); i++) {
			elementData[i] = sortiert.get(i);
		}
	}

	/**
	 * Pr&uuml;ft ob der Index nicht gr&ouml;&szlig;er als die Liste ist. Die
	 * Methode wurde aus {@link java.util.ArrayList} &uuml;bernommen.
	 * 
	 * @param index
	 *            Zu pr&uuml;fender Index
	 */
	private void checkIndex(int index) {
		if (index >= size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
					+ size);
		}
	}

	/**
	 * Initialisiert den internen Buffer.
	 * 
	 * @see java.util.ArrayList#ArrayList()
	 */
	public SortierteListe() {
		this(10);
	}

	/**
	 * Initialisiert den internen Buffer.
	 * 
	 * @param initialCapacity
	 *            Anfangsgr&ouml;&szlig; des Buffers
	 * 
	 * @see java.util.ArrayList#ArrayList(int)
	 */
	public SortierteListe(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Illegal Capacity: "
					+ initialCapacity);
		}

		elementData = (E[]) new Comparable<?>[initialCapacity];
	}

	/**
	 * Initialisiert den internen Buffer und f&uuml;gt anschlie&szlig;end die
	 * Elemente der {@code Collection} der neuen Liste hinzu.
	 * 
	 * @param c
	 *            Initialelemente der Liste
	 */
	public SortierteListe(Collection<? extends E> c) {
		size = c.size();
		// Allow 10% room for growth
		int capacity = (int) Math.min((size * 110L) / 100, Integer.MAX_VALUE);
		elementData = (E[]) c.toArray(new Comparable<?>[capacity]);
		mergesort();
	}

	/**
	 * Increases the capacity of this <tt>ArrayList</tt> instance, if
	 * necessary, to ensure that it can hold at least the number of elements
	 * specified by the minimum capacity argument.
	 * 
	 * @param minCapacity
	 *            the desired minimum capacity.
	 * @see java.util.ArrayList#ensureCapacity(int)
	 */
	@SuppressWarnings("unchecked")
	public void ensureCapacity(int minCapacity) {
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
			Object[] oldData = elementData;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}
			elementData = (E[]) new Comparable<?>[newCapacity];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(E o) {
		ensureCapacity(size + 1);
		elementData[size++] = o;
		mergesort();
		return true;
	}

	/**
	 * Gibt das Element mit einem bestimmten Index zur&uuml;ck.
	 * 
	 * @param index
	 *            Index des Elements
	 * @return Das Element
	 */
	@Override
	public E get(int index) {
		checkIndex(index);
		return elementData[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E remove(int index) {
		checkIndex(index);

		modCount++;
		E oldValue = elementData[index];

		int numMoved = size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(elementData, index + 1, elementData, index,
					numMoved);
		}
		elementData[--size] = null;

		return oldValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Gibt immer {@code null} zur&uuml;ck.
	 * <p>
	 * {@inheritDoc}
	 */
	public Comparator<? super E> comparator() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public E first() {
		return elementData[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public SortedSet<E> headSet(E toElement) {
		return subSet(first(), toElement);
	}

	/**
	 * {@inheritDoc}
	 */
	public E last() {
		return elementData[size - 1];
	}

	/**
	 * {@inheritDoc}
	 */
	public SortedSet<E> subSet(E fromElement, E toElement) {
		SortierteListe<E> liste;

		liste = new SortierteListe<E>();
		for (int i = 0; i < size; i++) {
			if (elementData[i].compareTo(fromElement) >= 0
					&& elementData[i].compareTo(toElement) < 0) {
				liste.add(elementData[i]);
			}
		}

		return liste;
	}

	/**
	 * {@inheritDoc}
	 */
	public SortedSet<E> tailSet(E fromElement) {
		return subSet(fromElement, last());
	}

}

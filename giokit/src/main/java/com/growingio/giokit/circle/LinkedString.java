package com.growingio.giokit.circle;

import java.lang.ref.WeakReference;

/**
 * 使用链表替代基于Array的String
 * 为了减少对象的分配与复制, 此String与标准String与很多不同， 使用该类前， 请确保了解该类的源码
 * Created by liangdengke on 2018/12/18.
 */
public class LinkedString {

    private LinkedString mHeadLinkedString;
    private LinkedNode mHead, mTail;
    private int size;
    private WeakReference<String> valueRef;
    private int hash;   // The cached hash value

    private static class LinkedNode{
        private LinkedNode next;
        private String value;
    }

    public LinkedString append(String str){
        if (str.length() == 0)
            return this;
        LinkedNode node = new LinkedNode();
        node.value = str;
        size += str.length();
        if (mHead == null){
            mHead = node;
            mTail = node;
        }else{
            mTail.next = node;
            mTail = node;
        }
        valueRef = null;
        hash = 0;
        return this;
    }

    public int length(){
        return size;
    }

    public LinkedStringIterator iterator(){
        return new LinkedStringIterator(0);
    }

    public LinkedString append(Object any){
        if (any == null)
            return this;
        return append(any.toString());
    }

    public String toStringValue(){
        if (valueRef != null && valueRef.get() != null){
            return valueRef.get();
        }

        if (mHeadLinkedString == null && mHead == mTail){
            // 为了LinkedString.fromString 生成的String提供一个快速的方法
            valueRef = new WeakReference<>(mHead.value);
            return valueRef.get();
        }

        StringBuilder builder = new StringBuilder(length());
        if (mHeadLinkedString != null){
            builder.append(mHeadLinkedString.toStringValue());
        }

        if (mHead != null){
            LinkedNode current = mHead;
            while (current != null){
                builder.append(current.value);
                if (current != mTail){
                    current = current.next;
                }else{
                    break;
                }
            }
        }
        String result = builder.toString();
        valueRef = new WeakReference<>(result);
        return result;
    }

    public boolean endsWith(String end){
        if (end.length() > size)
            return false;
        LinkedStringIterator iterator = new LinkedStringIterator(size - end.length());
        int charIndex = 0;
        if (!iterator.hasNext())
            return false;
        while (iterator.hasNext()){
            if (end.charAt(charIndex) != iterator.next())
                return false;
            charIndex++;
        }
        return true;
    }

    public char first(){
        if (mHeadLinkedString != null && mHeadLinkedString.size > 0)
            return mHeadLinkedString.first();
        if (mHead == null)
            throw new IllegalStateException("mHead should not be null");
        return mHead.value.charAt(0);
    }

    public char end(){
        if (mTail == null && mHeadLinkedString != null){
            return mHeadLinkedString.end();
        }
        if (mTail == null){
            throw new IllegalStateException("mTail should not be null");
        }
        return mTail.value.charAt(mTail.value.length() - 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LinkedString){
            if (length() != ((LinkedString) obj).length())
                return false;
            LinkedStringIterator iterator = iterator();
            LinkedStringIterator objIterator = ((LinkedString) obj).iterator();
            while (iterator.hasNext()){
                if (!objIterator.hasNext()
                        || iterator.next() != objIterator.next())
                    return false;
            }
            return !objIterator.hasNext();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0 && size != 0){
            LinkedStringIterator iterator = iterator();
            while (iterator.hasNext()){
                h = 31 * h + iterator.next();
            }
            hash = h;
        }
        return h;
    }

    /**
     * Not Deep copy, make sure you know everything
     */
    public static LinkedString copy(LinkedString linkedString){
        LinkedString result = new LinkedString();
        if (linkedString != null){
            result.mHeadLinkedString = linkedString;
            result.size = linkedString.size;
            result.valueRef = linkedString.valueRef;
        }
        return result;
    }

    @Override
    public String toString() {
        // TODO: 这个方法不应该被使用， 作为一个最后的保护
        return toStringValue();
    }

    public static LinkedString fromString(String value){
        LinkedString result = new LinkedString();
        if (value != null){
            result.append(value);
            result.valueRef = new WeakReference<>(value);
        }
        return result;
    }

    public static void stringWithoutQuotation(StringBuilder out, String value){
        for (int i = 0, length = value.length(); i < length; i++) {
            char c = value.charAt(i);
            handleChar(out, c);
        }
    }

    private static void handleChar(StringBuilder out, char ch){
        /*
         * From RFC 4627, "All Unicode characters may be placed within the
         * quotation marks except for the characters that must be escaped:
         * quotation mark, reverse solidus, and the control characters
         * (U+0000 through U+001F)."
         */
        switch (ch) {
            case '"':
            case '\\':
            case '/':
                out.append('\\').append(ch);
                break;

            case '\t':
                out.append("\\t");
                break;

            case '\b':
                out.append("\\b");
                break;

            case '\n':
                out.append("\\n");
                break;

            case '\r':
                out.append("\\r");
                break;

            case '\f':
                out.append("\\f");
                break;

            default:
                if (ch <= 0x1F) {
                    out.append(String.format("\\u%04x", (int) ch));
                } else {
                    out.append(ch);
                }
                break;
        }
    }


    public class LinkedStringIterator{
        private LinkedNode currentNode;
        private int currentIndex;
        private int mCurrentStrOffset;
        private boolean hasNext = false;
        private LinkedStringIterator headIterator;

        private LinkedStringIterator(int offset){
            this.mCurrentStrOffset = offset;
            if (mHeadLinkedString != null){
                if (this.mCurrentStrOffset >= mHeadLinkedString.size){
                    // 比头String还要大
                    this.mCurrentStrOffset = this.mCurrentStrOffset - mHeadLinkedString.size;
                }else{
                    headIterator = mHeadLinkedString.new LinkedStringIterator(this.mCurrentStrOffset);
                    this.mCurrentStrOffset = 0;
                }
            }

            if (headIterator == null){
                firstCalRightIndex();
            }else{
                hasNext = true;
            }
        }

        private void firstCalRightIndex() {
            calculateRightCurrentNodeByOffset();
            findRightIndex();
        }

        public boolean hasNext(){
            return hasNext;
        }

        public char next(){
            boolean headHasNex = headIterator != null && headIterator.hasNext();

            char result;
            if (headHasNex){
                // 有Head
                result = headIterator.next();
                if (!headIterator.hasNext()){
                    firstCalRightIndex();
                }
            }else{
                // 没有head
                result = currentNode.value.charAt(currentIndex);
                currentIndex++;
                findRightIndex();
            }
            return result;
        }

        private void calculateRightCurrentNodeByOffset(){
            int currentOffset = 0;
            LinkedNode current = mHead;
            int endIndex;
            while (current != null){
                endIndex = currentOffset + current.value.length();
                if (endIndex > mCurrentStrOffset){
                    // 节点发生在该Node
                    currentIndex = mCurrentStrOffset - currentOffset;
                    break;
                }
                if (current == mTail){
                    // overt
                    current = null;
                }else{
                    currentOffset += current.value.length();
                    current = current.next;
                }
            }
            currentNode = current;
        }

        private void findRightIndex(){
            if (currentNode == null)
                return;
            if (currentIndex == currentNode.value.length()){
                // 可能会越界
                currentIndex = 0;
                if (currentNode == mTail){
                    // over
                    currentNode = null;
                }else{
                    currentNode = currentNode.next;
                }
            }
            hasNext = currentNode != null;
        }
    }
}

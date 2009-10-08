// Autogenerated AST node
package org.python.pydev.parser.jython.ast;
import org.python.pydev.parser.jython.SimpleNode;

public final class Compare extends exprType implements cmpopType {
    public exprType left;
    public int[] ops;
    public exprType[] comparators;

    public Compare(exprType left, int[] ops, exprType[] comparators) {
        this.left = left;
        this.ops = ops;
        this.comparators = comparators;
    }

    public Compare(exprType left, int[] ops, exprType[] comparators, SimpleNode parent) {
        this(left, ops, comparators);
        this.beginLine = parent.beginLine;
        this.beginColumn = parent.beginColumn;
    }

    public Compare createCopy() {
        int[] new0;
        if(this.ops != null){
            new0 = new int[this.ops.length];
            System.arraycopy(this.ops, 0, new0, 0, this.ops.length);
        }else{
            new0 = this.ops;
        }
        exprType[] new1;
        if(this.comparators != null){
        new1 = new exprType[this.comparators.length];
        for(int i=0;i<this.comparators.length;i++){
            new1[i] = (exprType) this.comparators[i].createCopy();
        }
        }else{
            new1 = this.comparators;
        }
        Compare temp = new Compare(left!=null?(exprType)left.createCopy():null, new0, new1);
        temp.beginLine = this.beginLine;
        temp.beginColumn = this.beginColumn;
        if(this.specialsBefore != null){
            for(Object o:this.specialsBefore){
                if(o instanceof commentType){
                    commentType commentType = (commentType) o;
                    temp.getSpecialsBefore().add(commentType);
                }
            }
        }
        if(this.specialsAfter != null){
            for(Object o:this.specialsAfter){
                if(o instanceof commentType){
                    commentType commentType = (commentType) o;
                    temp.getSpecialsAfter().add(commentType);
                }
            }
        }
        return temp;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("Compare[");
        sb.append("left=");
        sb.append(dumpThis(this.left));
        sb.append(", ");
        sb.append("ops=");
        sb.append(dumpThis(this.ops, cmpopType.cmpopTypeNames));
        sb.append(", ");
        sb.append("comparators=");
        sb.append(dumpThis(this.comparators));
        sb.append("]");
        return sb.toString();
    }

    public Object accept(VisitorIF visitor) throws Exception {
        return visitor.visitCompare(this);
    }

    public void traverse(VisitorIF visitor) throws Exception {
        if (left != null)
            left.accept(visitor);
        if (comparators != null) {
            for (int i = 0; i < comparators.length; i++) {
                if (comparators[i] != null)
                    comparators[i].accept(visitor);
            }
        }
    }

}

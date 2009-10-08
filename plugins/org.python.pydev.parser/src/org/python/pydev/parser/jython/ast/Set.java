// Autogenerated AST node
package org.python.pydev.parser.jython.ast;
import org.python.pydev.parser.jython.SimpleNode;

public final class Set extends exprType {
    public exprType[] elts;

    public Set(exprType[] elts) {
        this.elts = elts;
    }

    public Set(exprType[] elts, SimpleNode parent) {
        this(elts);
        this.beginLine = parent.beginLine;
        this.beginColumn = parent.beginColumn;
    }

    public Set createCopy() {
        exprType[] new0;
        if(this.elts != null){
        new0 = new exprType[this.elts.length];
        for(int i=0;i<this.elts.length;i++){
            new0[i] = (exprType) this.elts[i].createCopy();
        }
        }else{
            new0 = this.elts;
        }
        Set temp = new Set(new0);
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
        StringBuffer sb = new StringBuffer("Set[");
        sb.append("elts=");
        sb.append(dumpThis(this.elts));
        sb.append("]");
        return sb.toString();
    }

    public Object accept(VisitorIF visitor) throws Exception {
        return visitor.visitSet(this);
    }

    public void traverse(VisitorIF visitor) throws Exception {
        if (elts != null) {
            for (int i = 0; i < elts.length; i++) {
                if (elts[i] != null)
                    elts[i].accept(visitor);
            }
        }
    }

}

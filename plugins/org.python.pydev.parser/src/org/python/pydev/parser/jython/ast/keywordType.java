// Autogenerated AST node
package org.python.pydev.parser.jython.ast;
import org.python.pydev.parser.jython.SimpleNode;

public final class keywordType extends SimpleNode {
    public NameTokType arg;
    public exprType value;

    public keywordType(NameTokType arg, exprType value) {
        this.arg = arg;
        this.value = value;
    }

    public keywordType(NameTokType arg, exprType value, SimpleNode parent) {
        this(arg, value);
        this.beginLine = parent.beginLine;
        this.beginColumn = parent.beginColumn;
    }

    public keywordType createCopy() {
        keywordType temp = new keywordType(arg!=null?(NameTokType)arg.createCopy():null,
        value!=null?(exprType)value.createCopy():null);
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
        StringBuffer sb = new StringBuffer("keyword[");
        sb.append("arg=");
        sb.append(dumpThis(this.arg));
        sb.append(", ");
        sb.append("value=");
        sb.append(dumpThis(this.value));
        sb.append("]");
        return sb.toString();
    }

    public Object accept(VisitorIF visitor) throws Exception {
        traverse(visitor);
        return null;
    }

    public void traverse(VisitorIF visitor) throws Exception {
        if (arg != null)
            arg.accept(visitor);
        if (value != null)
            value.accept(visitor);
    }

}

package org.python.pydev.parser.prettyprinterv2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Assert;
import org.python.pydev.core.Tuple;
import org.python.pydev.core.structure.FastStringBuffer;
import org.python.pydev.parser.jython.ISpecialStrOrToken;
import org.python.pydev.parser.jython.SimpleNode;
import org.python.pydev.parser.jython.Token;
import org.python.pydev.parser.jython.ast.commentType;
import org.python.pydev.parser.jython.ast.stmtType;

/**
 * This document is the temporary structure we create to put on the tokens and the comments.
 * 
 * It's line oriented and we should fill it with all things in the proper place (and properly indented) so
 * that we can just make a simple print later on.
 */
public class PrettyPrinterDocV2 {

    public final SortedMap<Integer, PrettyPrinterDocLineEntry> linesToColAndContents = new TreeMap<Integer, PrettyPrinterDocLineEntry>();
    
    private Map<Integer, List<ILinePart>> recordedChanges = new HashMap<Integer, List<ILinePart>>();
    private int lastRecordedChangesId=0;
    
    
    public void addBefore(int beginLine, int beginCol, String string, Object token) {
        PrettyPrinterDocLineEntry lineContents = getLine(beginLine);
        ILinePart linePart = lineContents.addBefore(beginCol, string, token);
        addToCurrentRecordedChanges(linePart);
    }


    private void addToCurrentRecordedChanges(ILinePart linePart) {
        for(List<ILinePart> lst:recordedChanges.values()){
            lst.add(linePart);
        }
    }

    
    public void add(int beginLine, int beginCol, String string, Object token) {
        PrettyPrinterDocLineEntry lineContents = getLine(beginLine);
        ILinePart linePart = lineContents.add(beginCol, string, token);
        addToCurrentRecordedChanges(linePart);
    }
    
    
    public LinePartRequireMark addRequireOneOf(SimpleNode node, String ... requireOneOf) {
        PrettyPrinterDocLineEntry line = getLine(node.beginLine);
        LinePartRequireMark linePart = line.addRequireMark(node.beginColumn, requireOneOf);
        addToCurrentRecordedChanges(linePart);
        return linePart;
    }

    public LinePartRequireMark addRequire(String string, SimpleNode node) {
        PrettyPrinterDocLineEntry line = getLine(node.beginLine);
        LinePartRequireMark linePart = line.addRequireMark(node.beginColumn, string);
        addToCurrentRecordedChanges(linePart);
        return linePart;
    }
    
    public LinePartRequireMark addRequireBefore(String string, ILinePart o1) {
        PrettyPrinterDocLineEntry line = getLine(o1.getLine());
        LinePartRequireMark linePart = line.addRequireMarkBefore(o1, string);
        addToCurrentRecordedChanges(linePart);
        return linePart;
    }

    public LinePartRequireMark addRequireAfter(String string, ILinePart o1) {
        PrettyPrinterDocLineEntry line = getLine(o1.getLine());
        LinePartRequireMark linePart = line.addRequireMarkAfterBefore(o1, string);
        addToCurrentRecordedChanges(linePart);
        return linePart;
    }

    
    public LinePartRequireIndentMark addRequireIndent(String string, SimpleNode node) {
        PrettyPrinterDocLineEntry line = getLine(node.beginLine);
        LinePartRequireIndentMark linePart = line.addRequireIndentMark(node.beginColumn, string);
        addToCurrentRecordedChanges(linePart);
        return linePart;
    }
    
    
    //---------------- Mark that a statement has started (new lines need a '\')
    
    public void addStartStatementMark(ILinePart foundWithLowerLocation, stmtType node) {
        getLine(foundWithLowerLocation.getLine()).addStartStatementMark(foundWithLowerLocation, node);
    }


    public void addEndStatementMark(ILinePart foundWithHigherLocation, stmtType node) {
        getLine(foundWithHigherLocation.getLine()).addEndStatementMark(foundWithHigherLocation, node);
    }
    
    
    //------------ Get information

    PrettyPrinterDocLineEntry getLine(int beginLine) {
        PrettyPrinterDocLineEntry lineContents = linesToColAndContents.get(beginLine);
        if(lineContents == null){
            lineContents = new PrettyPrinterDocLineEntry(beginLine);
            linesToColAndContents.put(beginLine, lineContents);
        }
        return lineContents;
    }

    int getLastLineKey() {
        return linesToColAndContents.lastKey();
    }
    
    PrettyPrinterDocLineEntry getLastLine() {
        Integer lastKey = linesToColAndContents.lastKey();
        if(lastKey != null){
            return linesToColAndContents.get(lastKey);
        }
        return null;
    }
    
    
    
    public ILinePart getLastPart() {
        PrettyPrinterDocLineEntry lastLine = getLastLine();
        java.util.List<ILinePart> sortedParts = lastLine.getSortedParts();
        return sortedParts.get(sortedParts.size()-1);
    }

    
    
    
    
    //------------ Indentation
    public void addIndent(SimpleNode node) {
        PrettyPrinterDocLineEntry line = getLine(node.beginLine);
        line.indent(node);
    }
    
    public LinePartIndentMark addIndent(SimpleNode node, boolean requireNewLine) {
        PrettyPrinterDocLineEntry line = getLine(node.beginLine);
        return line.indent(node, requireNewLine);
    }
    
    
    public void addIndent(Token token, boolean requireNewLine) {
        PrettyPrinterDocLineEntry line = getLine(token.beginLine);
        line.indent(token, requireNewLine);
    }    


    public void addDedent() {
        addDedent(0);
    }
    
    public void addDedent(int emptyLinesRequiredAfterDedent) {
        PrettyPrinterDocLineEntry lastLine = getLastLine();
        lastLine.dedent(emptyLinesRequiredAfterDedent);
    }
    
    
    
    
    

    //------------ toString

    @Override
    public String toString() {
        FastStringBuffer buf = new FastStringBuffer();
        buf.append("PrettyPrinterDocV2[\n");
        Set<Entry<Integer, PrettyPrinterDocLineEntry>> entrySet = linesToColAndContents.entrySet();
        for(Entry<Integer, PrettyPrinterDocLineEntry> entry:entrySet){
            buf.append(entry.getKey()+": "+entry.getValue()+"\n");
        }
        return "PrettyPrinterDocV2["+buf+"]";
    }
    
    
    
    
    //------------ Changes Recording

    public int pushRecordChanges() {
        lastRecordedChangesId++;
        recordedChanges.put(lastRecordedChangesId, new ArrayList<ILinePart>());
        return lastRecordedChangesId;
    }

    public List<ILinePart> popRecordChanges(int id) {
        List<ILinePart> ret = recordedChanges.remove(id);
        return ret;
    }


    public int replaceRecorded(List<ILinePart> recordChanges, String ... replacements) {
        int replaced = 0;
        Assert.isTrue(replacements.length % 2 == 0);
        for(ILinePart linePart:recordChanges){
            if(linePart instanceof ILinePart2){
                ILinePart2 iLinePart2 = (ILinePart2) linePart;
                for(int i=0;i<replacements.length;i+=2){
                    String toReplace = replacements[i]; 
                    String newToken = replacements[i+1];
                    if(iLinePart2.getString().equals(toReplace)){
                        iLinePart2.setString(newToken);
                        replaced += 1;
                    }
                }
            }
        }
        return replaced;
    }


    public Tuple<ILinePart, ILinePart> getLowerAndHigerFound(List<ILinePart> recordChanges) {
        return getLowerAndHigerFound(recordChanges, true);
    }
    
    public Tuple<ILinePart, ILinePart> getLowerAndHigerFound(List<ILinePart> recordChanges, boolean acceptToken) {
        Tuple<ILinePart, ILinePart> lowerAndHigher = null;
        ILinePart foundWithLowerLocation=null;
        ILinePart foundWithHigherLocation=null;
        
        for(ILinePart p:recordChanges){
            if(p.getToken() instanceof commentType){
                continue;
            }
            if(!acceptToken){
                if(p.getToken() instanceof ISpecialStrOrToken){
                    continue;
                }
            }
            if(foundWithHigherLocation==null){
                foundWithHigherLocation = p;
                
            }else if(p.getLine() > foundWithHigherLocation.getLine()){
                foundWithHigherLocation = p;
                    
            }else if(p.getLine() == foundWithHigherLocation.getLine() && p.getBeginCol() > foundWithHigherLocation.getBeginCol()){
                foundWithHigherLocation = p;
            }
            
            
            if(foundWithLowerLocation==null){
                foundWithLowerLocation = p;
                
            }else if(p.getLine() < foundWithLowerLocation.getLine()){
                foundWithLowerLocation = p;
                
            }else if(p.getLine() == foundWithLowerLocation.getLine() && p.getBeginCol() < foundWithLowerLocation.getBeginCol()){
                foundWithLowerLocation = p;
            }
        }
        if(foundWithLowerLocation != null && foundWithHigherLocation != null){
            lowerAndHigher = new Tuple<ILinePart, ILinePart>(foundWithLowerLocation, foundWithHigherLocation);
        }
        return lowerAndHigher;
    }


    
    
    
    
    
    public void checkTokenAdded(List<ILinePart> changes, SimpleNode node, String string) {
        for(ILinePart p:changes){
            if(p instanceof ILinePart2){
                ILinePart2 iLinePart2 = (ILinePart2) p;
                if(iLinePart2.getString().equals(string)){
                    return;
                }
            }
        }
        this.addBefore(node.beginLine, node.beginColumn, string, node);
    }


    /**
     * In this method, all the require marks have to be either already given in the parsing
     * (and removed from the line) or should be replaced by actual nodes.
     */
    public void validateRequireMarks() {
        if(linesToColAndContents.size() == 0){
            return;//nothing to validate (no entries there)
        }
        for(int line=linesToColAndContents.firstKey();line<=linesToColAndContents.lastKey();line++){
            PrettyPrinterDocLineEntry prettyPrinterDocLineEntry = linesToColAndContents.get(line);
            if(prettyPrinterDocLineEntry == null){
                continue;
            }
            List<ILinePart> parts = prettyPrinterDocLineEntry.getSortedParts();
            for(int position=0;position<parts.size();position++){
                ILinePart iLinePart = parts.get(position);
                if(iLinePart instanceof LinePartRequireMark){
                    LinePartRequireMark linePartRequireMark = (LinePartRequireMark) iLinePart;
                    
                    //Ok, go forwards and see if we have a match somewhere
                    Tuple<ILinePart, Boolean> search = search(line, position, linePartRequireMark, true);
                    boolean found = search.o2;
                    if(!found){
                        search = search(line, position, linePartRequireMark, false);
                        found = search.o2;
                    }
                    
                    ILinePart next=search.o1;
                    
                    if(!found){
                        if(iLinePart instanceof LinePartRequireIndentMark){
                            throw new RuntimeException("Unable to find place to add indent");
                        }
                        ILinePart removed = parts.remove(position);
                        parts.add(position, new LinePartRequireAdded(
                                removed.getBeginCol(), 
                                linePartRequireMark.getToken(), 
                                linePartRequireMark.getToken(), 
                                prettyPrinterDocLineEntry));
                    }else{
                        if(iLinePart instanceof LinePartRequireIndentMark){
                            //add the indent on the last position returned
                            PrettyPrinterDocLineEntry l = this.getLine(next.getLine());
                            l.indentAfter(next, true);
                        }
                        int i = parts.indexOf(iLinePart);
                        parts.remove(i);
                        position--; //make up for the removed part
                    }
                }
            }
        }
        
        
    }


    private Tuple<ILinePart, Boolean> search(int line, int position, LinePartRequireMark linePartRequireMark, boolean forward) {
        boolean found = false;
        ILinePart next = null;
        LinePartsIterator it = getLinePartsIterator(line, position, forward);
        boolean searchForIndentMark = linePartRequireMark instanceof LinePartRequireIndentMark;
        
        OUTER:
        while(it.hasNext() && !found){
            next = it.next();
            if(next instanceof ILinePart2){
                if(!searchForIndentMark && next instanceof LinePartRequireAdded){
                    break; //As the require parts are in order, finding a previously added require, we just mark it as not found.
                }
                ILinePart2 part2 = (ILinePart2) next;
                if(linePartRequireMark.requireOneOf != null){
                    for(String s:linePartRequireMark.requireOneOf){
                        if(part2.getString().equals(s)){
                            found = true;
                            break OUTER;
                        }
                    }
                }else{
                    if(part2.getString().equals(linePartRequireMark.getToken())){
                        found = true;
                        break;
                    }
                }
                
                Object token = next.getToken();
                if(token instanceof SimpleNode && !(token instanceof commentType)){
                    break; //didn't find it.
                }
            }
        }
        return new Tuple<ILinePart, Boolean>(next, found);
    }


    public LinePartsIterator getLinePartsIterator(int initialLine, int initialPos, boolean forward) {
        return new LinePartsIterator(this, initialLine, initialPos, forward);
    }










//    public void checkTokenAt(SimpleNode node, String string) {
//        PrettyPrinterDocLineEntry line = this.getLine(node.beginLine);
//        List<ILinePart> sortedParts = line.getSortedParts();
//        for(ILinePart iLinePart:sortedParts){
//            if(iLinePart instanceof ILinePart2){
//                ILinePart2 iLinePart2 = (ILinePart2) iLinePart;
//                if(iLinePart2.getBeginCol() == node.beginColumn && iLinePart2.getString().equals(string)){
//                    return;
//                }
//                if(iLinePart2.getBeginCol() > node.beginColumn){
//                    break;
//                }
//            }
//        }
//        this.add(node.beginLine, node.beginColumn, string, node);
//    }
//
//
//    public void checkTokenAfterAndBefore(SimpleNode after, SimpleNode before, String string) {
//        OUT:
//        for(int iLine=after.beginLine; iLine<=before.beginLine; iLine++){
//            PrettyPrinterDocLineEntry line = this.getLine(iLine);
//            
//            List<ILinePart> sortedParts = line.getSortedParts();
//            for(ILinePart iLinePart:sortedParts){
//                
//                if(iLinePart.getLine() == after.beginLine && iLinePart.getBeginCol() < after.beginColumn){
//                    continue; //still didn't reach it.
//                }
//                
//                if(iLinePart.getLine() == before.beginLine && iLinePart.getBeginCol() > before.beginColumn){
//                    break OUT; //we passed the before node.
//                }
//                
//                if(iLinePart instanceof ILinePart2){
//                    ILinePart2 iLinePart2 = (ILinePart2) iLinePart;
//                    if(iLinePart2.getString().equals(string)){
//                        return;
//                    }
//                }
//            }
//        }
//        this.add(after.beginLine, after.beginColumn, string, after);
//    }



    
}


class LinePartsIterator implements Iterator<ILinePart>{
    
    private int line;
    private int position;
    
    List<ILinePart> currentPart;
    private PrettyPrinterDocV2 doc;
    private ILinePart next;
    private boolean forward;
    
    
    public LinePartsIterator(PrettyPrinterDocV2 prettyPrinterDocV2, int initialLine, int initialPos, boolean forward) {
        this.doc = prettyPrinterDocV2;
        this.line = initialLine;
        this.position = initialPos;
        this.forward = forward;
        calcNext();
    }


    private void calcNext() {
        next = null;
        
        if(forward){
            for(;line<=doc.linesToColAndContents.lastKey();line++){
                PrettyPrinterDocLineEntry prettyPrinterDocLineEntry = doc.linesToColAndContents.get(line);
                if(prettyPrinterDocLineEntry == null){
                    continue;
                }
                List<ILinePart> parts = prettyPrinterDocLineEntry.getSortedParts();
                while(next == null){
                    if(position<parts.size()){
                        next = parts.get(position);
                        position ++;
                        return;
                    }
                    if(position>=parts.size()){
                        position = 0;
                        break;
                    }
                }
            }
        }else{//backward
            for(;line>=doc.linesToColAndContents.firstKey();line--){
                PrettyPrinterDocLineEntry prettyPrinterDocLineEntry = doc.linesToColAndContents.get(line);
                if(prettyPrinterDocLineEntry == null){
                    continue;
                }
                List<ILinePart> parts = prettyPrinterDocLineEntry.getSortedParts();
                while(next == null){
                    if(position>=parts.size()){
                        position = parts.size()-1;
                    }
                    if(position>=0){
                        next = parts.get(position);
                        position --;
                        return;
                    }
                    if(position<0){
                        position = Integer.MAX_VALUE;
                        break;
                    }
                }
            }
        }
        
    }

    public void remove() {
        throw new RuntimeException("Not impl");
    }

    
    @Override
    public ILinePart next() {
        ILinePart ret = next;
        calcNext();
        return ret;
    }
    
    @Override
    public boolean hasNext() {
        return next != null;
    }

}
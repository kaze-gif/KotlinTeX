package io.github.darriousliu.katex.mathdisplay.parse

class MTMathList {
    internal var atoms = mutableListOf<MTMathAtom>()


    internal constructor(vararg alist: MTMathAtom) {
        for (atom in alist) {
            atoms.add(atom)
        }
    }

    internal constructor(alist: MutableList<MTMathAtom>) {
        atoms.addAll(alist)
    }

    private fun isAtomAllowed(atom: MTMathAtom): Boolean {
        return atom.type != MTMathAtomType.KMTMathAtomBoundary
    }

    internal fun addAtom(atom: MTMathAtom?) {
        if (atom == null) return
        if (!isAtomAllowed(atom)) {
            val s = MTMathAtom.typeToText(atom.type)
            throw MathDisplayException("Cannot add atom of type $s in a mathList ")
        }
        atoms.add(atom)
    }

    internal fun insertAtom(atom: MTMathAtom, index: Int) {
        if (!isAtomAllowed(atom)) {
            val s = MTMathAtom.typeToText(atom.type)
            throw MathDisplayException("Cannot add atom of type $s in a mathList ")
        }
        atoms.add(index, atom)
    }

    internal fun append(list: MTMathList) {
        atoms.addAll(list.atoms)
    }

    override fun toString(): String {
        val str = StringBuilder()
        for (atom in this.atoms) {
            str.append(atom.toString())
        }
        return str.toString()
    }

    fun description(): String {
        return (this.toString())
    }

    internal fun finalized(): MTMathList {
        val newList = MTMathList()
        val zeroRange = NSRange(0, 0)

        var prevNode: MTMathAtom? = null
        for (atom in this.atoms) {
            val newNode = atom.finalized()
            var skip = false  // Skip adding this node it has been fused
            // Each character is given a separate index.
            if (zeroRange.equal(atom.indexRange)) {
                val index: Int = if (prevNode == null) {
                    0
                } else {
                    prevNode.indexRange.location + prevNode.indexRange.length
                }
                newNode.indexRange = NSRange(index, 1)
            }

            when (newNode.type) {
                MTMathAtomType.KMTMathAtomBinaryOperator -> {
                    if (MTMathAtom.isNotBinaryOperator(prevNode)) {
                        newNode.type = MTMathAtomType.KMTMathAtomUnaryOperator
                    }
                }

                MTMathAtomType.KMTMathAtomRelation, MTMathAtomType.KMTMathAtomPunctuation, MTMathAtomType.KMTMathAtomClose -> {
                    if (prevNode != null && prevNode.type == MTMathAtomType.KMTMathAtomBinaryOperator) {
                        prevNode.type = MTMathAtomType.KMTMathAtomUnaryOperator
                    }
                }

                MTMathAtomType.KMTMathAtomNumber -> {
                    // combine numbers together
                    if (prevNode != null && prevNode.type == MTMathAtomType.KMTMathAtomNumber && prevNode.subScript == null && prevNode.superScript == null) {
                        prevNode.fuse(newNode)
                        // skip the current node, we are done here.
                        skip = true
                    }
                }

                else -> {
                    // Do nothing
                }
            }
            if (!skip) {
                newList.addAtom(newNode)
                prevNode = newNode
            }
        }
        if (prevNode != null && prevNode.type == MTMathAtomType.KMTMathAtomBinaryOperator) {
            // it isn't a binary since there is noting after it. Make it a unary
            prevNode.type = MTMathAtomType.KMTMathAtomUnaryOperator
        }
        return newList
    }

    internal fun copyDeep(): MTMathList {
        val newList = MTMathList()
        for (atom in this.atoms) {
            newList.addAtom(atom.copyDeep())
        }
        return newList
    }
}



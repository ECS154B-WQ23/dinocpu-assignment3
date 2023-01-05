#!/usr/bin/env python3

# Copyright (c) 2022 The Regents of the University of California
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are
# met: redistributions of source code must retain the above copyright  
# notice, this list of conditions and the following disclaimer;
# redistributions in binary form must reproduce the above copyright
# notice, this list of conditions and the following disclaimer in the
# documentation and/or other materials provided with the distribution;
# neither the name of the copyright holders nor the names of its
# contributors may be used to endorse or promote products derived from
# this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
# "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
# LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
# A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
# OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
# LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
# DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
# THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

import argparse
from collections import deque
import numpy as np
import sys

class Options:
    def __init__(self, seed, dim_size, data_type):
        self.seed = seed
        self.dim_size = dim_size
        self.data_type = data_type

class CFileGenerator:
    def __init__(self, filename):
        self.filename = filename
        self.lines = deque()
    def prepend_line(self, line):
        self.lines.appendleft(line)
    def append_line(self, line):
        self.lines.append(line)
    def gen_define(self, symbol, val=""):
        if not val:
            return " ".join(["#define", symbol])
        return " ".join(["#define", symbol, val])
    def gen_header_guard(self, name):
        return [" ".join(["#ifndef", name]),
                self.gen_define(name),
                " ".join(["#endif //", name])
               ]
    def gen_typedef(self, _type, name):
        return " ".join(["typedef", _type, name, ";"])
    def gen_static_array(self, _type, name, size, elements, num_elements_per_line=16):
        lines = []
        lines.append(f"static {_type} {name}[{size}] =")
        lines.append(f"{{")
        for idx_line in range((len(elements) + num_elements_per_line - 1)//num_elements_per_line):
            curr_line = []
            for idx_element in range(idx_line * num_elements_per_line, (idx_line+1)*num_elements_per_line):
                curr_line.append(f"{elements[idx_element]:>4},")
            lines.append("".join(curr_line))
        lines.append(f"}};")
        return lines
    def gen_comment(self, comment):
        return f"// {comment}"
    def gen_empty_line(self):
        return ""
    def add_define(self, symbol, val=""):
        self.append_line(self.gen_define(symbol, val))
    def add_typedef(self, _type, name):
        self.append_line(self.gen_typedef(_type, name))
    def add_static_array(self, _type, name, size, elements, num_elements_per_line=16):
        for line in self.gen_static_array(_type, name, size, elements, num_elements_per_line):
            self.append_line(line)
    def add_empty_line(self):
        self.append_line(self.gen_empty_line())
    def add_header_guard(self, name):
        guard = self.gen_header_guard(name)
        self.prepend_line(guard[1])
        self.prepend_line(guard[0])
        self.append_line(guard[2])
    def add_comment(self, comment):
        self.append_line(self.gen_comment(comment))
    def emit_file(self):
        with open(self.filename, "w") as f:
            f.write("\n".join(self.lines))

class MatrixGenerator:
    def __init__(self, seed):
        self.seed = options.seed
        self.rng = np.random.default_rng(seed=seed)
    def generate_elements(self, n_elements, element_range):
        min_element, max_element = element_range
        ans = self.rng.integers(max_element - min_element + 1, size = n_elements)
        ans += min_element
        return ans

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--dim-size", required=True, type=int,
                        help="Size of a side of a square matrix.")
    parser.add_argument("--output-name", required=True, type=str,
                        help="Name of the output file.")
    parser.add_argument("--data-type", required=True, type=str,
                        help="Data type of each element in the matrices, e.g. int, long.")

    args = parser.parse_args()

    options = Options(seed=154, dim_size=args.dim_size, data_type=args.data_type)

    matrixgenerator = MatrixGenerator(seed = options.seed)
    matrix_a = matrixgenerator.generate_elements(options.dim_size**2, element_range = (0, 2))
    matrix_b = matrixgenerator.generate_elements(options.dim_size**2, element_range = (0, 2))
    matrix_c = np.matmul(matrix_a.reshape(options.dim_size, options.dim_size), matrix_b.reshape(options.dim_size, options.dim_size))
    matrix_c = matrix_c.reshape(options.dim_size**2)

    cfilegenerator = CFileGenerator(args.output_name)
    cfilegenerator.add_comment("Command: {}".format(" ".join(sys.argv)))
    cfilegenerator.add_define("ARRAY_SIZE", str(options.dim_size**2))
    cfilegenerator.add_define("DIM_SIZE", str(options.dim_size))
    cfilegenerator.add_typedef(options.data_type, "data_t")
    cfilegenerator.add_static_array("data_t", "input1_data", options.dim_size**2, list(map(str, list(matrix_a))), options.dim_size)
    cfilegenerator.add_static_array("data_t", "input2_data", options.dim_size**2, list(map(str, list(matrix_b))), options.dim_size)
    cfilegenerator.add_static_array("data_t", "verify_data", options.dim_size**2, list(map(str, list(matrix_c))), options.dim_size)
    cfilegenerator.add_header_guard("__DATASET_H")
    cfilegenerator.emit_file()

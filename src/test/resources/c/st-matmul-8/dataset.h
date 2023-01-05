#ifndef __DATASET_H
#define __DATASET_H
// Command: ../st-matmul-32/matmul_gendata.py --output-name=dataset.h --data-type=int --dim-size=8
#define ARRAY_SIZE 64
#define DIM_SIZE 8
typedef int data_t ;
static data_t input1_data[64] =
{
   0,   1,   1,   1,   2,   0,   1,   0,
   0,   2,   2,   0,   0,   1,   0,   2,
   0,   2,   1,   0,   1,   1,   0,   2,
   1,   2,   0,   1,   1,   0,   2,   2,
   0,   2,   0,   0,   0,   2,   0,   1,
   2,   1,   0,   2,   0,   2,   1,   0,
   2,   2,   0,   1,   2,   0,   1,   1,
   1,   1,   1,   1,   2,   1,   1,   2,
};
static data_t input2_data[64] =
{
   1,   0,   0,   2,   2,   1,   0,   1,
   0,   1,   1,   1,   0,   0,   0,   2,
   2,   0,   2,   1,   0,   2,   2,   1,
   2,   0,   1,   0,   2,   2,   0,   1,
   0,   1,   2,   0,   0,   2,   1,   2,
   2,   2,   1,   1,   1,   1,   0,   1,
   1,   1,   2,   2,   2,   2,   2,   0,
   0,   0,   0,   0,   2,   0,   2,   1,
};
static data_t verify_data[64] =
{
   5,   4,  10,   4,   4,  10,   6,   8,
   6,   4,   7,   5,   5,   5,   8,   9,
   4,   5,   7,   4,   5,   5,   7,  10,
   5,   5,   9,   8,  12,   9,   9,  10,
   4,   6,   4,   4,   4,   2,   2,   7,
  11,   6,   7,   9,  12,  10,   2,   8,
   5,   5,   9,   8,  10,  10,   6,  12,
   8,   6,  11,   7,  11,  12,  10,  12,
};
#endif // __DATASET_H
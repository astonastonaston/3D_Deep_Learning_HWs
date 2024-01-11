# Journey to 3D Deep Learning

This repo gives the journey that a little white goes through to learn about 3D understanding and reconstruction topics. He took 3D Deep Learning course at UCSD and learnt a lot on deep learning (writing a data loader, magically modifying a network, implementing loss functions with Tensors for back propagations, etc) using PyTorch. He learnt 3D fundamentals including surface and normal differentials, curvatures, Gaussians and Means, etc, too. 


He actually didn't do well in some assignments: ICP and PointNet-based pose estimation performances are poor on the benchmark (~37% accuracy at maximum), and till now he still doesn't know the reason why some can get 80%+ accuracy. He also feels deeply how he has much improvement space in terms of deep learning experiences as well as 3D topics. He sincerely hopes that someone can give some suggestions on his codes, point out some improvements, or just tell him some tricks that can improve the performance of ICP/PointNet.


It includes codes for the following assignments:

* Basic optimizations and probability. Uniform sampling over triangles

* Rotation representations. Differential geometry representations of a surface. Mesh geometry computations. Point Cloud sampling and Gaussian curvatures' computations.

* Shape deformation based on Chamfer loss and curvature loss.

* 6D Pose estimation based on ICP and (PointNet-like) learning methods.

* Novel View Synthesis with NeRF and TensoRF.

Hit a star if you like my works.

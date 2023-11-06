


# def best_fit_transform(source_pcd, target_pcd):
#     '''
#     Calculates the least-squares best-fit transform that maps corresponding points A to B in m spatial dimensions
#     Input:
#       A: Nxm numpy array of corresponding points
#       B: Nxm numpy array of corresponding points
#     Returns:
#       T: (m+1)x(m+1) homogeneous transformation matrix that maps A on to B
#       R: mxm rotation matrix
#       t: mx1 translation vector
#     '''

#     assert source_pcd.shape == target_pcd.shape

#     # get number of dimensions (3 for this scenario)
#     m = source_pcd.shape[1]

#     # translate points to their centroids
#     pbar = np.mean(source_pcd, axis=0)
#     qbar = np.mean(target_pcd, axis=0)
#     srcDist = source_pcd - pbar
#     tgDist = target_pcd - qbar

#     # rotation matrix
#     H = np.dot(srcDist.T, tgDist)
#     U, S, Vt = np.linalg.svd(H)
#     R = np.dot(Vt.T, U.T)

#     # special reflection case
#     if np.linalg.det(R) == -1:
#        Vt[m-1,:] *= -1
#        R = np.dot(Vt.T, U.T)

#     # translation
#     t = qbar.T - np.dot(R,pbar.T)

#     # homogeneous transformation
#     T = np.identity(m+1)
#     T[:m, :m] = R
#     T[:m, m] = t

#     return R, t


# import numpy as np
# from sklearn.neighbors import NearestNeighbors


# def best_fit_transform(source_pcd, target_pcd):
#     '''
#     Calculates the least-squares best-fit transform that maps corresponding points A to B in m spatial dimensions
#     Input:
#       A: Nxm numpy array of corresponding points
#       B: Nxm numpy array of corresponding points
#     Returns:
#       T: (m+1)x(m+1) homogeneous transformation matrix that maps A on to B
#       R: mxm rotation matrix
#       t: mx1 translation vector
#     '''

#     assert source_pcd.shape == target_pcd.shape

#     # get number of dimensions (3 for this scenario)
#     m = source_pcd.shape[1]

#     # translate points to their centroids
#     pbar = np.mean(source_pcd, axis=0)
#     qbar = np.mean(target_pcd, axis=0)
#     srcDist = source_pcd - pbar
#     tgDist = target_pcd - qbar

#     # rotation matrix
#     H = np.dot(srcDist.T, tgDist)
#     U, S, Vt = np.linalg.svd(H)
#     R = np.dot(Vt.T, U.T)

#     # special reflection case
#     if np.linalg.det(R) == -1:
#        Vt[m-1,:] *= -1
#        R = np.dot(Vt.T, U.T)

#     # translation
#     t = qbar.T - np.dot(R,pbar.T)

#     # homogeneous transformation
#     T = np.identity(m+1)
#     T[:m, :m] = R
#     T[:m, m] = t

#     return T


# def nearest_neighbor(src, dst):
#     '''
#     Find the nearest (Euclidean) neighbor in dst for each point in src
#     Input:
#         src: Nxm array of points
#         dst: Nxm array of points
#     Output:
#         distances: Euclidean distances of the nearest neighbor
#         indices: dst indices of the nearest neighbor
#     '''

#     assert src.shape == dst.shape

#     neigh = NearestNeighbors(n_neighbors=1)
#     neigh.fit(dst)
#     distances, indices = neigh.kneighbors(src, return_distance=True)
#     return distances.ravel(), indices.ravel()


# def icp(A, B):
#     '''
#     The Iterative Closest Point method: finds best-fit transform that maps points A on to points B
#     Input:
#         A: Nxm numpy array of source mD points
#         B: Nxm numpy array of destination mD point
#         init_pose: (m+1)x(m+1) homogeneous transformation
#         max_iterations: exit algorithm after max_iterations
#         tolerance: convergence criteria
#     Output:
#         T: final homogeneous transformation that maps A on to B
#         distances: Euclidean distances (errors) of the nearest neighbor
#         i: number of iterations to converge
#     '''

#     assert A.shape == B.shape

#     # get number of dimensions
#     m = A.shape[1]
#     max_iterations = 20
#     tolerance = 0.00001
#     init_pose = None

#     # make points homogeneous, copy them to maintain the originals
#     src = np.ones((m+1,A.shape[0]))
#     dst = np.ones((m+1,B.shape[0]))
#     src[:m,:] = np.copy(A.T)
#     dst[:m,:] = np.copy(B.T)

#     # apply the initial pose estimation
#     if init_pose is not None:
#         src = np.dot(init_pose, src)

#     prev_error = 0
#     num_ite = 0

#     for i in range(max_iterations):
#         # find the nearest neighbors between the current source and destination points
#         distances, indices = nearest_neighbor(src[:m,:].T, dst[:m,:].T)

#         # compute the transformation between the current source and nearest destination points
#         T = best_fit_transform(src[:m,:].T, dst[:m,indices].T)

#         # update the current source
#         src = np.dot(T, src)

#         # check error
#         mean_error = np.mean(distances)
#         if np.abs(prev_error - mean_error) < tolerance:
#             print("Tolerance reached")
#             break
#         prev_error = mean_error
#         num_ite += 1

#     # calculate final transformation
#     print("Iteration round: {}".format(num_ite))
#     T = best_fit_transform(A, src[:m,:].T)

#     return T

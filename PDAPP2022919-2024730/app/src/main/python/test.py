#import python_speech_features
#import scipy.io.wavfile as wav
import numpy as np
#import scipy.stats
import os
#import opensmile
'''
def feature_extract(file_path):
    features = []
    (rate, sig) = wav.read(file_path)
    
    mfcc_feat = python_speech_features.mfcc(sig, rate, nfft=2048)
    mfcc_size = 13
    for i in range(mfcc_size):
        features.append(np.average(mfcc_feat[:, i]))
        features.append(np.std(mfcc_feat[:, i]))
        features.append(scipy.stats.skew(mfcc_feat[:, i]))
        features.append(scipy.stats.kurtosis(mfcc_feat[:, i]))
    

    smile = opensmile.Smile(
        feature_set=opensmile.FeatureSet.eGeMAPSv02,
        feature_level=opensmile.FeatureLevel.Functionals,
    )
    smile_features = smile.process_file(file_path).to_numpy()
    #print("smile : ", len(smile_features[0, :]))
    features.extend(smile_features[0, :])
    return features
'''








def test( file_path ):
    return file_path+"_test_ok"

def add_numbers(a = 1, b = 1):

    return a + b


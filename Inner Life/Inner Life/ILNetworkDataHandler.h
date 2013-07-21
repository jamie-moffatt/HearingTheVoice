//
//  ILNetworkDataHandler.h
//  Inner Life
//
//  Created by Matthew Bates on 15/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ILSchedule.h"

@interface ILNetworkDataHandler : NSObject

+ (NSArray *)downloadAndParseQuestionsInSections;
+ (ILSchedule *)downloadAndParseSchedule;

@end

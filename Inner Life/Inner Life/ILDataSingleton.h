//
//  ILDataSingleton.h
//  Inner Life
//
//  Created by Matthew Bates on 15/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ILQuestionXMLParser.h"
#import "ILScheduleXMLParser.h"

@interface ILDataSingleton : NSObject

@property NSInteger currentQuestion;
@property NSInteger currentSection;

@property NSMutableDictionary *prevResponseIDs;
@property NSMutableDictionary *prevResponseStrings;
@property NSMutableDictionary *prevResponseValues;
@property NSMutableArray *prevSessions;

// Return an array of sections
- (NSArray *)getQuestions;
// Return a schedule object either from the cache or by accessing the web API
- (ILSchedule *)getSchedule;

@end
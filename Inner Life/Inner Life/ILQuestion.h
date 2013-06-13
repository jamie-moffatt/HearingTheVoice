//
//  ILQuestion.h
//  Inner Life
//
//  Created by Matthew Bates on 12/06/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum ILQuestionTypeEnum
{
    RADIO,
    YESNO,
    NUMSCALE
} ILQuestionType;

@interface ILQuestion : NSObject

@property NSInteger questionID;
@property NSInteger sectionID;
@property NSInteger number;
@property ILQuestionType type;
@property NSString *questionDescription;

@end

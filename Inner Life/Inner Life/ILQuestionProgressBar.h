//
//  ILQuestionProgressBar.h
//  Inner Life
//
//  Created by Matthew Bates on 22/07/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ILQuestionProgressBar : UIView
{
    @private
    UIImage *current;
    UIImage *incomplete;
    UIImage *complete;
    
}

@property (nonatomic) NSInteger currentSection;
@property (nonatomic) NSInteger currentSubSection;
@property (nonatomic) NSArray *sectionSpecification;

@end
